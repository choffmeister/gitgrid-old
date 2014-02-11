package com.gitgrid

import akka.actor._
import spray.http._
import com.gitgrid.git._
import com.gitgrid.webapi.WebApiServiceActor

import java.io._
import spray.io._
import scala.concurrent.duration._
import spray.can.Http.RegisterChunkHandler

class HttpServiceActor extends Actor with ActorLogging {
  val actorRefFactory = context
  val webApiHttpService = context.actorOf(Props[WebApiServiceActor], "webapi-http-service")
  val gitHttpService = context.actorOf(Props[GitHttpServiceActor], "git-http-service")

  def receive = {
    case akka.io.Tcp.Connected(_, _) =>
      sender ! akka.io.Tcp.Register(self)
    case req@HttpRequest(_, uri, _, _, _) if uri.path.startsWith(Uri.Path("/api")) =>
      log.debug(req.toString())
      webApiHttpService.tell(req, sender)

    case req@GitHttpRequest(_, _, _, _) =>
      /*println("================================================")
      println("UNCHUNKED GIT REQUEST")
      println("================================================")
      val client = sender
      val parts = req.asPartStream()
      val handler = context.actorOf(Props(new GitHttpRequestHandler(client, parts.head.asInstanceOf[ChunkedRequestStart])))
      parts.tail.foreach(handler !)*/

      println("================================================")
      println("GIT HTTP REQ")
      println("================================================")
      log.debug(req.toString())
      gitHttpService.tell(req, sender)

    case req@ChunkedRequestStart(GitHttpRequest(_, _, _, _)) =>
      println("================================================")
      println("CHUNKED GIT REQUEST")
      println("================================================")
      val client = sender
      val handler = context.actorOf(Props(new GitHttpRequestHandler(client, req)))
      sender ! RegisterChunkHandler(handler)

      /*println("================================================")
      println("CHUNKED GIT HTTP REQ")
      println("================================================")
      log.debug(req.toString())
      gitHttpService.tell(req, sender)*/

    case o =>
      log.debug("Unknown message received: " + o.toString())
      sender ! HttpResponse(status = 404)
  }
}
