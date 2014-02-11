package com.gitgrid.git

import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import akka.actor._
import spray.can.Http
import spray.can.server.Stats
import spray.util._
import spray.http._
import spray.http.StatusCodes._
import spray.httpx.encoding._
import HttpMethods._
import HttpHeaders._
import spray.http.ContentType
import spray.can.Http.RegisterChunkHandler
import CacheDirectives._
import java.io._
import org.eclipse.jgit.transport.{UploadPack, ReceivePack}
import com.gitgrid.util.ZipHelper
import com.gitgrid.mongodb._
import com.gitgrid.Config

class GitHttpServiceActor extends Actor {
  import scala.concurrent.ExecutionContext.Implicits.global

  def receive = {
    case _: Http.Connected =>
      sender ! Http.Register(self)

    case req@GitHttpRequest(_, _, "info/refs", None) =>
      sender ! HttpResponse(status = 403, entity = "Git dump HTTP protocol is not supported")

    case req@GitHttpRequest(namespace, name, "info/refs", Some("git-upload-pack")) =>
      openRepository(namespace, name, sender) { repo =>
        val in = decodeRequest(req).entity.data.toByteArray
        val out = uploadPack(repo, in, true) // must be true, since else sendAdvertisedRefs is not invoked
        encodeResponse(HttpResponse(entity = HttpEntity(GitHttpService.gitUploadPackAdvertisement, GitHttpService.gitUploadPackHeader ++ out), headers = GitHttpService.noCacheHeaders), req.acceptedEncodingRanges)
      }

    case req@GitHttpRequest(namespace, name, "info/refs", Some("git-receive-pack")) =>
      openRepository(namespace, name, sender) { repo =>
        val in = decodeRequest(req).entity.data.toByteArray
        val out = receivePack(repo, in, true) // must be true, since else sendAdvertisedRefs is not invoked
        encodeResponse(HttpResponse(entity = HttpEntity(GitHttpService.gitReceivePackAdvertisement, GitHttpService.gitReceivePackHeader ++ out), headers = GitHttpService.noCacheHeaders), req.acceptedEncodingRanges)
      }

    case req@GitHttpRequest(namespace, name, "git-upload-pack", None) =>
      openRepository(namespace, name, sender) { repo =>
        val in = decodeRequest(req).entity.data.toByteArray
        val out = uploadPack(repo, in, false)
        encodeResponse(HttpResponse(entity = HttpEntity(GitHttpService.gitUploadPackResult, out), headers = GitHttpService.noCacheHeaders), req.acceptedEncodingRanges)
      }

    case req@GitHttpRequest(namespace, name, "git-receive-pack", None) =>
      openRepository(namespace, name, sender) { repo =>
        val in = decodeRequest(req).entity.data.toByteArray
        val out = receivePack(repo, in, false)
        encodeResponse(HttpResponse(entity = HttpEntity(GitHttpService.gitUploadPackResult, out), headers = GitHttpService.noCacheHeaders), req.acceptedEncodingRanges)
      }

    case _ =>
      sender ! HttpResponse(BadRequest)
  }

  private def openRepository(userName: String, canonicalName: String, sender: ActorRef)(inner: GitRepository => HttpResponse) = {
    Projects.findByFullQualifiedName(userName, canonicalName).map { project =>
      project match {
        case Some(project) =>
          val dir = new File(Config.repositoriesDir, project.id.get.stringify)
          if (dir.exists()) sender ! GitRepository(dir)(inner)
          else sender ! HttpResponse(InternalServerError)
        case _ =>
          sender ! HttpResponse(NotFound)
      }
    }
  }

  private def uploadPack(repo: GitRepository, in: Array[Byte], biDirectionalPipe: Boolean): Array[Byte] = {
    val up = new UploadPack(repo.jgit)
    up.setBiDirectionalPipe(biDirectionalPipe)
    val is = new ByteArrayInputStream(in)
    val os = new ByteArrayOutputStream()
    up.upload(is, os, null)
    os.toByteArray
  }

  private def receivePack(repo: GitRepository, in: Array[Byte], biDirectionalPipe: Boolean): Array[Byte] = {
    val rp = new ReceivePack(repo.jgit)
    rp.setBiDirectionalPipe(biDirectionalPipe)
    val is = new ByteArrayInputStream(in)
    val os = new ByteArrayOutputStream()
    rp.receive(is, os, null)
    os.toByteArray
  }

  private def encodeResponse(res: HttpResponse, acceptedEncodingRanges: List[HttpEncodingRange]): HttpResponse = {
    @scala.annotation.tailrec
    def encode(res: HttpResponse, encoders: List[Encoder]): HttpResponse = encoders match {
      case first :: more if acceptedEncodingRanges.exists(_.matches(first.encoding)) => first.encode(res)
      case first :: more => encode(res, more)
      case Nil => res
    }

    encode(res, List(Gzip, Deflate))
  }

  private def decodeRequest(req: HttpRequest): HttpRequest = {
    @scala.annotation.tailrec
    def decode(req: HttpRequest, decoders: List[Decoder]): HttpRequest = decoders match {
      case first :: more if first.encoding == req.encoding => first.decode(req)
      case first :: more => decode(req, more)
      case Nil => throw new Exception(s"Encoding '${req.encoding}' is not supported")
    }

    decode(req, List(Gzip, Deflate, NoEncoding))
  }
}

object GitHttpService {
  val noCacheHeaders = List(`Cache-Control`(`no-cache`, `max-age`(0), `must-revalidate`))
  val gitUploadPackHeader = "001e# service=git-upload-pack\n0000".getBytes("ASCII")
  val gitReceivePackHeader = "001f# service=git-receive-pack\n0000".getBytes("ASCII")
  val gitUploadPackAdvertisement = spray.http.MediaTypes.register(
    MediaType.custom(
      mainType = "application",
      subType = "x-git-upload-pack-advertisement",
      compressible = false,
      binary = true,
      fileExtensions = Seq()))
  val gitUploadPackResult = spray.http.MediaTypes.register(
    MediaType.custom(
      mainType = "application",
      subType = "x-git-upload-pack-result",
      compressible = false,
      binary = true,
      fileExtensions = Seq()))
  val gitReceivePackAdvertisement = spray.http.MediaTypes.register(
    MediaType.custom(
      mainType = "application",
      subType = "x-git-receive-pack-advertisement",
      compressible = false,
      binary = true,
      fileExtensions = Seq()))
  val gitReceivePackResult = spray.http.MediaTypes.register(
    MediaType.custom(
      mainType = "application",
      subType = "x-git-receive-pack-result",
      compressible = false,
      binary = true,
      fileExtensions = Seq()))
}

object GitHttpRequest {
  val pattern = """^/([a-zA-Z0-9\-\_]+)/([a-zA-Z0-9\-\_]+)\.git/(.*)$""".r

  def unapply(req: HttpRequest): Option[(String, String, String, Option[String])] = {
    req.uri.path.toString match {
      case pattern(repositoryNamespace, repositoryName, action) =>
        Some((repositoryNamespace, repositoryName, action, req.uri.query.get("service")))
      case _ => None
    }
  }
}
