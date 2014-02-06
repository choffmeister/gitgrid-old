package com.gitgrid.models

import org.squeryl.Query
import org.squeryl.KeyedEntityDef

import com.gitgrid.models._
import com.gitgrid.models.Dsl._

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
