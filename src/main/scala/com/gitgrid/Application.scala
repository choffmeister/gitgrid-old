package com.gitgrid

import akka.actor.ActorSystem
import akka.actor.Props
import akka.io.IO
import spray.can.Http
import com.gitgrid.webservice.WebServiceActor

object Application extends App {
  // generate test data
  TestDataGenerator.generate()

  // start webservice
  implicit val system = ActorSystem("gitgrid")

  val service = system.actorOf(Props[WebServiceActor], "webservice")

  IO(Http) ! Http.Bind(service, interface = "localhost", port = 8080)
}
