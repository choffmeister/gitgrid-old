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
}