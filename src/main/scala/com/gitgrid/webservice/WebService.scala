package com.gitgrid.webservice

import akka.actor._
import com.gitgrid.managers._
import com.gitgrid.models._
import spray.http._
import spray.http.CacheDirectives._
import spray.routing._

class WebServiceActor extends Actor with WebService {
  def actorRefFactory = context
  def receive = runRoute(route)
}

trait WebService extends HttpService {
  import JsonProtocol._
  implicit val timeout = akka.util.Timeout(1000)
  implicit def executionContext = actorRefFactory.dispatcher

  val route =
    pathPrefix("api") {
      respondWithHeader(HttpHeaders.`Cache-Control`(`no-cache`, `max-age`(0))) {
        pathPrefix("auth")(AuthenticationRoutes.route) ~
        pathPrefix("projects" / LongNumber / "git")(projectId => GitRoutes.create(projectId)) ~
        CrudRoutes.create("users", UserManager) ~
        CrudRoutes.create("projects", ProjectManager) ~
        CrudRoutes.create("tickets", TicketManager, beforeCreate = Some((t: Ticket, u: User) => t.copy(creatorId = u.id, createdAt = UserManager.now)))
      }
    } ~
    path(Rest) {
      case filePath if filePath.length > 0 => getFromResource("web/" + filePath)
      case _ => getFromResource("web/index.html")
    }
}
