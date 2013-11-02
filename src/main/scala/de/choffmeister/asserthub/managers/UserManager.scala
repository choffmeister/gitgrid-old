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