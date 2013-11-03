package de.choffmeister.asserthub.managers

import org.squeryl.Query

import de.choffmeister.asserthub.models._
import de.choffmeister.asserthub.models.Dsl._

object UserManager {
  /**
   * Returns list of all users ordered by their IDs.
   */
  def allUsers: List[User] = inTransaction(from(Database.users)(u => select(u) orderBy(u.id asc)).toList)
  
  /**
   * Creates a new user and persists it to the database.
   */
  def createUser(userName: String, email: String, password: String, hashAlgorithm: String = "plain"): User = {
    hashAlgorithm match {
      case "plain" =>
        Database.users.insert(new User(0L, userName, email, password, "", "plain"))
      case x =>
        throw new Exception(s"Unknown hash algorithm ${x}")
    }
  }
  
  /**
   * Returns the User object if credentials are valid or else None.
   */
  def authenticate(userName: String, password: String): Option[User] = {
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