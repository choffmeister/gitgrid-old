package com.gitgrid.webapi

import akka.actor._
import spray.http._
import spray.routing._

class StaticContentHttpServiceActor extends Actor with StaticContentHttpService {
  def actorRefFactory = context
  def receive = runRoute(route)
}

trait StaticContentHttpService extends HttpService {
  implicit val timeout = akka.util.Timeout(1000)
  implicit val executionContext = actorRefFactory.dispatcher

  val route =
    path(Rest) {
      case filePath if filePath.length > 0 => getFromResource("web/" + filePath)
      case _ => getFromResource("web/index.html")
    }
}
