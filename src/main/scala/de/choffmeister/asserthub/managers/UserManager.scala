package de.choffmeister.asserthub.managers

import org.squeryl.Query

import de.choffmeister.asserthub.models._
import de.choffmeister.asserthub.models.Dsl._

object UserManager extends EntityRepository[User] {
  def all: List[User] = inTransaction(from(Database.users)(u => select(u) orderBy(u.id asc)).toList)
  
  def find(id: Long): Option[User] = inTransaction(Database.users.lookup(id))
  
  def insert(user: User): User = inTransaction(Database.users.insert(user))
    
  def update(user: User): Unit = inTransaction(Database.users.update(user))
  
  def delete(id: Long): Unit = inTransaction(Database.users.deleteWhere(u => u.id === id))
  
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