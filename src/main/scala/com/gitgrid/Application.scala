package com.gitgrid

import org.squeryl.adapters.H2Adapter
import akka.actor.ActorSystem
import akka.actor.Props
import akka.io.IO
import com.gitgrid.models._
import spray.can.Http
import com.gitgrid.webservice.WebServiceActor

object Application extends App {
  // bootstrap database
  Database.createFactory(new H2Adapter(), "org.h2.Driver", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")

  // generate test data
  TestDataGenerator.generate()

  // start webservice
  implicit val system = ActorSystem("gitgrid")
  val httpService = system.actorOf(Props[HttpServiceActor], "http-service")
  IO(Http) ! Http.Bind(httpService, interface = "localhost", port = 8080)
}
