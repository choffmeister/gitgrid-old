package com.gitgrid.git

import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import akka.actor._
import spray.can.Http
import spray.can.server.Stats
import spray.util._
import spray.http._
import HttpMethods._
import HttpHeaders._
import spray.http.ContentType
import spray.can.Http.RegisterChunkHandler
import CacheDirectives._
import java.io._
import org.eclipse.jgit.transport.{UploadPack, ReceivePack}
import com.gitgrid.util.ZipHelper

class SmartHttpService extends Actor {
  implicit val timeout: Timeout = 1.second // for the actor 'asks'
  import context.dispatcher // ExecutionContext for the futures and scheduler

  val repoDir1 = new File(System.getProperty("java.io.tmpdir"), java.util.UUID.randomUUID.toString)
  val repoDir2 = new File(System.getProperty("java.io.tmpdir"), java.util.UUID.randomUUID.toString)
  ZipHelper.unzip(this.getClass.getResourceAsStream("/gitignore.zip"), repoDir1)
  ZipHelper.unzip(this.getClass.getResourceAsStream("/highlightjs.zip"), repoDir2)
  val repoDirMap = Map("gitignore" -> repoDir1, "highlightjs" -> repoDir2)

  val noCacheHeaders = List(`Cache-Control`(`no-cache`, `max-age`(0), `must-revalidate`))

  def receive = {
    case _: Http.Connected =>
      sender ! Http.Register(self)

    case req@SmartHttpRequest(_, _, "info/refs", None) =>
      sender ! HttpResponse(status = 403, entity = "Git dump HTTP protocol is not supported")

    case req@SmartHttpRequest(namespace, name, "info/refs", Some("git-upload-pack")) =>
      val in = req.entity.data.toByteArray
      val out = GitRepository(repoDirMap(name))(repo => uploadPack(repo, in, true)) // must be true, since else sendAdvertisedRefs is not invoked
      sender ! HttpResponse(entity = HttpEntity(SmartHttpService.gitUploadPackAdvertisement, SmartHttpService.gitUploadPackHeader ++ out), headers = noCacheHeaders)

    case req@SmartHttpRequest(namespace, name, "info/refs", Some("git-receive-pack")) =>
      val in = req.entity.data.toByteArray
      val out = GitRepository(repoDirMap(name))(repo => receivePack(repo, in, true)) // must be true, since else sendAdvertisedRefs is not invoked
      sender ! HttpResponse(entity = HttpEntity(SmartHttpService.gitReceivePackAdvertisement, SmartHttpService.gitReceivePackHeader ++ out), headers = noCacheHeaders)

    case req@SmartHttpRequest(namespace, name, "git-upload-pack", None) =>
      val in = req.entity.data.toByteArray
      val out = GitRepository(repoDirMap(name))(repo => uploadPack(repo, in, false))
      sender ! HttpResponse(entity = HttpEntity(SmartHttpService.gitUploadPackResult, out), headers = noCacheHeaders)

    case req@SmartHttpRequest(namespace, name, "git-receive-pack", None) =>
      val in = req.entity.data.toByteArray
      val out = GitRepository(repoDirMap(name))(repo => receivePack(repo, in, false))
      sender ! HttpResponse(entity = HttpEntity(SmartHttpService.gitUploadPackResult, out), headers = noCacheHeaders)

    case _ =>
      sender ! HttpResponse(status = 404)
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
}

object SmartHttpService {
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

object SmartHttpRequest {
  val pattern = """^/([a-zA-Z0-9\-\_]+)/([a-zA-Z0-9\-\_]+)\.git/(.*)$""".r

  def unapply(req: HttpRequest): Option[(String, String, String, Option[String])] = {
    req.uri.path.toString match {
      case pattern(repositoryNamespace, repositoryName, action) =>
        Some((repositoryNamespace, repositoryName, action, req.uri.query.get("service")))
      case _ => None
    }
  }
}
