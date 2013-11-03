package de.choffmeister.asserthub

import org.squeryl.adapters.H2Adapter

import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.io.IO
import de.choffmeister.asserthub.models._
import de.choffmeister.asserthub.models.Dsl._
import de.choffmeister.asserthub.managers.UserManager

import spray.can.Http

object Application extends App {
  // bootstrap database
  Database.createFactory(new H2Adapter(), "org.h2.Driver", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")

  transaction {
    Database.create

    val users = (1 to 5).map(i => UserManager.createUser(s"user${i}", s"user${i}@invalid.domain.tld", s"pass${i}"))
  }

  // start webservice
  implicit val system = ActorSystem("asserthub")

  val service = system.actorOf(Props[WebService], "webservice")

  IO(Http) ! Http.Bind(service, interface = "localhost", port = 8080)
}
