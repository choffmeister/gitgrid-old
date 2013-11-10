package de.choffmeister.asserthub

import org.squeryl.adapters.H2Adapter
import akka.actor.ActorSystem
import akka.actor.Props
import akka.io.IO
import de.choffmeister.asserthub.models._
import spray.can.Http

object Application extends App {
  // bootstrap database
  Database.createFactory(new H2Adapter(), "org.h2.Driver", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")

  // generate test data
  TestDataGenerator.generate()

  // start webservice
  implicit val system = ActorSystem("asserthub")

  val service = system.actorOf(Props[WebServiceActor], "webservice")

  IO(Http) ! Http.Bind(service, interface = "0.0.0.0", port = 8080)
}
