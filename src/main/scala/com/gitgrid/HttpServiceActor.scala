package com.gitgrid

import akka.actor._
import spray.http._
import com.gitgrid.git.GitHttpRequest
import com.gitgrid.git.GitHttpServiceActor
import com.gitgrid.webapi.WebApiServiceActor

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
      log.debug(req.toString())
      gitHttpService.tell(req, sender)
    case o =>
      log.debug("Unknown message received: " + o.toString())
      sender ! HttpResponse(status = 404)
  }
}
