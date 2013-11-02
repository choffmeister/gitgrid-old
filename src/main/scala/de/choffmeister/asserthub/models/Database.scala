package de.choffmeister.asserthub.models

import java.sql.DriverManager

import org.squeryl.Schema
import org.squeryl.Session
import org.squeryl.SessionFactory
import org.squeryl.internals.DatabaseAdapter

import de.choffmeister.asserthub.models.Dsl._

object Database extends Schema {
  val users = table[User]

  on(users)(u => declare(
    u.userName is(unique, indexed),
    u.email is(unique, indexed)
  ))

  def createFactory(adapter: DatabaseAdapter, driverClassName: String, connectionString: String): Unit = {
    Class.forName(driverClassName)

    SessionFactory.concreteFactory = Some(() =>
      Session.create(DriverManager.getConnection(connectionString), adapter))
  }
}
