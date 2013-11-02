package de.choffmeister.asserthub

import java.sql.DriverManager

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode.string2ScalarString
import org.squeryl.Schema
import org.squeryl.Session
import org.squeryl.SessionFactory
import org.squeryl.internals.DatabaseAdapter

class User(
  val id: Long,
  val userName: String,
  val email: String,
  val passwordHash: String,
  val passwordSalt: String,
  val passwordHashAlgorithm: String,
  val firstName: String,
  val lastName: String
) extends KeyedEntity[Long] {
  def this() = this(0, "", "", "", "", "", "", "")
  def this(userName: String, email: String, firstName: String, lastName: String) = this(0, userName, email, "", "", "", firstName, lastName)

  override def toString(): String = userName + "#" + id
}

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
