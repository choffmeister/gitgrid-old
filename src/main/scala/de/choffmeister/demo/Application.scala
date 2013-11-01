package de.choffmeister.asserthub

import akka.actor._
import akka.io.IO
import spray.can._
import spray.http._

class WebService extends Actor with ActorLogging {
  def receive = {
    case x: Http.Connected =>
      log.info(x.toString())
      sender ! Http.Register(self)
    case HttpRequest(HttpMethods.GET, Uri.Path("/ping"), _, _, _) =>
      sender ! HttpResponse(404, entity = "PONG")
    case x =>
      log.info(x.toString())
  }
}

object Application {
  //private implicit val system = ActorSystem()

  def main(args: Array[String]) {
    //val webService = system.actorOf(Props[WebService])

    //IO(Http) ! Http.Bind(webService, interface = "localhost", port = 8080)

    println("Hello World!")
  }
}
