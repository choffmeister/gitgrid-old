package com.gitgrid

import akka.actor.ActorSystem
import akka.actor.Props
import akka.io.IO
import spray.can.Http
import com.gitgrid.webapi.WebApiServiceActor

object Application extends App {
  // generate test data
  TestDataGenerator.generate()

  // start webservice
  implicit val system = ActorSystem("gitgrid")
  val httpService = system.actorOf(Props[HttpServiceActor], "http-service")
  IO(Http) ! Http.Bind(httpService, interface = "localhost", port = 8080)
}
