package com.gitgrid

import akka.actor._
import spray.http._
import com.gitgrid.git.SmartHttpService
import com.gitgrid.git.SmartHttpRequest
import com.gitgrid.webservice.WebServiceActor

class HttpServiceActor extends Actor with ActorLogging {
  val actorRefFactory = context
  val apiHttpService = context.actorOf(Props[WebServiceActor], "api-http-service")
  val gitHttpService = context.actorOf(Props[SmartHttpService], "git-http-service")

  def receive = {
    case akka.io.Tcp.Connected(_, _) =>
      sender ! akka.io.Tcp.Register(self)
    case req@HttpRequest(_, uri, _, _, _) if uri.path.startsWith(Uri.Path("/api")) =>
      log.debug(req.toString())
      apiHttpService.tell(req, sender)
    case req@SmartHttpRequest(_, _, _, _) =>
      log.debug(req.toString())
      gitHttpService.tell(req, sender)
    case o =>
      log.debug("Unknown message received: " + o.toString())
      sender ! HttpResponse(status = 404)
  }
}
