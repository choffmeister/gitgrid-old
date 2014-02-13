package com.gitgrid.webapi

import akka.actor._
import com.gitgrid.managers._
import com.gitgrid.mongodb._
import spray.http._
import spray.http.CacheDirectives._
import spray.routing._

class WebApiServiceActor extends Actor with WebApiService {
  def actorRefFactory = context
  def receive = runRoute(route)
}

trait WebApiService extends HttpService {
  import JsonProtocol._
  implicit val timeout = akka.util.Timeout(1000)
  implicit val executionContext = actorRefFactory.dispatcher
  implicit val authManager = new AuthManager(new InMemorySessionManager())

  val authRoutes = new AuthenticationRoutes()
  val usersCrudRoutes = new CrudRoutes("users", Users)
  val projectsCrudRoutes = new CrudRoutes("projects", Projects)
  val ticketsCrudRoutes = new CrudRoutes("tickets", Tickets)
  val gitRepositoryRoutes = new GitRoutes()

  val route =
    pathPrefix("api") {
      respondWithHeader(HttpHeaders.`Cache-Control`(`no-cache`, `max-age`(0))) {
        authRoutes.route ~
        usersCrudRoutes.route ~
        projectsCrudRoutes.route ~
        ticketsCrudRoutes.route ~
        gitRepositoryRoutes.route
      }
    } ~
    path(Rest) {
      case filePath if filePath.length > 0 => getFromResource("web/" + filePath)
      case _ => getFromResource("web/index.html")
    }
}
