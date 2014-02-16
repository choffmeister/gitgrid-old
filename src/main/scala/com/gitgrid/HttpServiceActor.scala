package com.gitgrid

import akka.actor._
import akka.routing._
import com.gitgrid.git._
import com.gitgrid.webapi._
import spray.http._
import spray.http.HttpMethods._
import spray.http.StatusCodes._

class HttpServiceActor extends Actor with ActorLogging {
  implicit val executionContext = context.dispatcher

  val webApiServiceActor = context.actorOf(Props[WebApiServiceActor].withRouter(FromConfig), "webapi")
  val gitHttpServiceActor = context.actorOf(Props[GitHttpServiceActor].withRouter(FromConfig), "git")
  val staticContentHttpServiceActor = context.actorOf(Props[StaticContentHttpServiceActor].withRouter(FromConfig), "staticcontent")

  def receive = {
    case akka.io.Tcp.Connected(_, _) =>
      sender ! akka.io.Tcp.Register(self)
    case req@HttpRequest(_, uri, _, _, _) if uri.path.startsWith(Uri.Path("/api")) =>
      log.debug(req.toString())
      webApiServiceActor.tell(req, sender)
    case req@GitHttpRequest(_, _, _, _) =>
      log.debug(req.toString())
      gitHttpServiceActor.tell(req, sender)
    case req@HttpRequest(GET, _, _, _, _) =>
      log.debug(req.toString())
      staticContentHttpServiceActor.tell(req, sender)
    case o =>
      log.debug("Unknown message received: " + o.toString())
      sender ! HttpResponse(status = NotFound)
  }
}
