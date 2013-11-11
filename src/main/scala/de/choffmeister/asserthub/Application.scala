package de.choffmeister.asserthub

import org.squeryl.adapters.H2Adapter
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.io.IO
import de.choffmeister.asserthub.managers._
import de.choffmeister.asserthub.models._
import de.choffmeister.asserthub.models.Dsl._
import spray.can.Http
import scala.util.Random
import java.sql.Timestamp

object Application extends App {
  // bootstrap database
  Database.createFactory(new H2Adapter(), "org.h2.Driver", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")

  transaction {
    Database.create
    
    val userCount = 5
    val projectCount = 10
    val ticketCount = 100

    val random = new Random()
    val users = (1 to userCount).map(i => UserManager.createUser(s"user${i}", s"user${i}@invalid.domain.tld", s"pass${i}"))
    val projects = (1 to projectCount).map(i => ProjectManager.createProject(s"P${i}", s"Project ${i}", random.nextInt(userCount) + 1))
    val tickets = (1 to ticketCount).map(i => TicketManager.createTicket(s"Ticket #${i}", s"This is ticket ${i}", random.nextInt(userCount) + 1))
  }

  // start webservice
  implicit val system = ActorSystem("asserthub")

  val service = system.actorOf(Props[WebServiceActor], "webservice")

  IO(Http) ! Http.Bind(service, interface = "localhost", port = 8080)
}
