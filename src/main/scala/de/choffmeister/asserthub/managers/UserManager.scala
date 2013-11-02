package de.choffmeister.asserthub.managers

import de.choffmeister.asserthub.Database
import de.choffmeister.asserthub.User
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Query

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
    val user = singleOrNone(Database.users.where(u => u.userName === userName.toLowerCase()))
    if (user.isDefined) {
      // check if password matches
      if (user.get.passwordHash == password) {
        return user
      }
    }
    
    None
  }
  
  private def singleOrNone[R](query: Query[R]): Option[R] = {
    val cappedList = query.take(1).toList
    
    if (cappedList.length == 1) Some(cappedList(0)) else None
  }
}