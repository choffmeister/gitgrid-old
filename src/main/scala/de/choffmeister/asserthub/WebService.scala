package de.choffmeister.asserthub

import akka.actor.{Actor, ActorLogging}
import spray.can._
import spray.http._
import de.choffmeister.asserthub.managers.UserManager

class WebService extends Actor with ActorLogging {
  def receive = {
    case _: Http.Connected =>
      sender ! Http.Register(self)
    case HttpRequest(HttpMethods.GET, Uri.Path("/ping"), _, _, _) =>
      sender ! HttpResponse(200, entity = "PONG")
    case HttpRequest(HttpMethods.GET, Uri.Path("/users"), _, _, _) =>
      sender ! HttpResponse(entity = UserManager.allUsers.mkString(", "))
    case HttpRequest(method, path, _, _, _) =>
      log.info(s"Not found $method $path")
      sender ! HttpResponse(404, entity = "Not found")
  }
}
