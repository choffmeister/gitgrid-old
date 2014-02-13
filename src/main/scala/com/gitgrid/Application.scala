package com.gitgrid

import akka.actor._
import akka.io.IO
import com.gitgrid.git.GitHttpRequest
import com.gitgrid.git.GitHttpServiceActor
import com.gitgrid.webapi.WebApiServiceActor
import scala.concurrent.duration._
import spray.can.Http
import spray.http._

class ApplicationActor extends Actor with ActorLogging {
  val actorRefFactory = context
  val webApiHttpService = context.actorOf(Props[WebApiServiceActor], "webapi-http-service")
  val gitHttpService = context.actorOf(Props[GitHttpServiceActor], "git-http-service")

  def receive = {
    case akka.io.Tcp.Connected(_, _) =>
      sender ! akka.io.Tcp.Register(self)
    case req@HttpRequest(_, uri, _, _, _) if uri.path.startsWith(Uri.Path("/api")) =>
      log.debug(req.toString())
      webApiHttpService.tell(req, sender)
    case req@GitHttpRequest(_, _, _, _) =>
      log.debug(req.toString())
      gitHttpService.tell(req, sender)
    case o =>
      log.debug("Unknown message received: " + o.toString())
      sender ! HttpResponse(status = 404)
  }
}

class Application extends Bootable {
  implicit val system = ActorSystem("gitgrid")

  def startup = {
    // generate test data
    TestDataGenerator.generate()

    // start webservice
    val httpService = system.actorOf(Props[ApplicationActor], "application")
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

trait Bootable {
  def startup(): Unit
  def shutdown(): Unit

  sys.ShutdownHookThread(shutdown)
}
