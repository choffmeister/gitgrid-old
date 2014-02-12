package com.gitgrid

import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.actor.Props
import akka.io.IO
import spray.can.Http

class Application extends Bootable {
  implicit val system = ActorSystem("gitgrid")

  def startup = {
    // generate test data
    TestDataGenerator.generate()

    // start webservice
    val httpService = system.actorOf(Props[HttpServiceActor], "http-service")
    IO(Http) ! Http.Bind(httpService, interface = Config.httpInterface, port = Config.httpPort)
  }

  def shutdown = {
    system.shutdown()
    system.awaitTermination(1.seconds)
  }
}

object Application {
  def main(args: Array[String]) {
    val app = new Application()
    app.startup()
  }
}
