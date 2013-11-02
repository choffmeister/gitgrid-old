package de.choffmeister.asserthub

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http

object Application extends App {
  implicit val system = ActorSystem("asserthub")

  val service = system.actorOf(Props[WebService], "webservice")

  IO(Http) ! Http.Bind(service, interface = "localhost", port = 8080)
}
