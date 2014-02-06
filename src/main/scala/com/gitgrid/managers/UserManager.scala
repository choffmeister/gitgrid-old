package com.gitgrid.managers

import com.gitgrid.models._
import com.gitgrid.models.Dsl._

object UserManager extends UserRepository {
  /**
   * Creates a new user and persists it to the database.
   */
  def createUser(userName: String, email: String, password: String, hashAlgorithm: String = "plain"): User = {
    hashAlgorithm match {
      case "plain" =>
        insert(new User(0L, userName, email, password, "", "plain"))
      case x =>
        throw new Exception(s"Unknown hash algorithm ${x}")
    }
  }

  /**
   * Returns the User object if credentials are valid or else None.
   */
  def authenticate(userName: String, password: String): Option[User] = {
    inTransaction {
      // search for user with matching name
      val user = Database.users.where(u => u.userName === userName.toLowerCase()).singleOption
      if (user.isDefined) {
        // check if password matches
        if (user.get.passwordHash == password) {
          return user
        }
      }

      None
    }
  }
}
