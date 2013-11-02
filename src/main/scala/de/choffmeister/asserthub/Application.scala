package de.choffmeister.asserthub

import akka.actor.{ ActorSystem, Props }
import akka.io.IO
import spray.can.Http
import org.squeryl.PrimitiveTypeMode.transaction
import org.squeryl.adapters.H2Adapter

object Application extends App {
  // bootstrap database
  Database.createFactory(new H2Adapter(), "org.h2.Driver", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")

  transaction {
    Database.create

    val users = for (i <- 1 to 5) yield Database.users.insert(new User("user" + i, "user" + i + "@invalid.domain.tld", "First" + i, "Last" + i))
  }

  // start webservice
  implicit val system = ActorSystem("asserthub")

  val service = system.actorOf(Props[WebService], "webservice")

  IO(Http) ! Http.Bind(service, interface = "localhost", port = 8080)
}
