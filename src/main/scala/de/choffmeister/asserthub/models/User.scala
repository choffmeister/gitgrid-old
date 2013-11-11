package de.choffmeister.asserthub.models

import org.squeryl.Query
import org.squeryl.KeyedEntityDef

import de.choffmeister.asserthub.models._
import de.choffmeister.asserthub.models.Dsl._

case class User(
  id: Long,
  userName: String,
  email: String,
  passwordHash: String,
  passwordSalt: String = "",
  passwordHashAlgorithm: String = "plain",
  firstName: String = "",
  lastName: String = ""
) extends Entity

trait UserRepository extends EntityRepository[User] {
  val keyDef = userKED
  val table = Database.users
}