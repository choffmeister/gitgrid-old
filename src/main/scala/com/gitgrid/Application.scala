package com.gitgrid

import akka.actor._
import akka.io.IO
import scala.concurrent.duration._
import spray.can.Http

class Application extends Bootable {
  implicit val system = ActorSystem("gitgrid")

  def startup = {
    // generate test data
    TestDataGenerator.generate()

    val httpServiceActor = system.actorOf(Props[HttpServiceActor], "httpservice")

    IO(Http) ! Http.Bind(httpServiceActor, interface = Config.httpInterface, port = Config.httpPort)
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

trait Bootable {
  def startup(): Unit
  def shutdown(): Unit

  sys.ShutdownHookThread(shutdown)
}
