package de.choffmeister.asserthub.managers

import org.specs2.mutable._

import de.choffmeister.asserthub.WithDatabase
import de.choffmeister.asserthub.models.Dsl.transaction
import de.choffmeister.asserthub.models.User

class UserManagerSpec extends SpecificationWithJUnit {
  def createUser(i: Int) = new User(0L, s"user${i}", s"user${i}@invalid.domain.tld", s"First${i}", s"Last${i}")
    
  "UserManager" should {
    "allow listing of all users" in new WithDatabase {
      transaction {
        db.drop
        db.create
        
        val users = (1 to 5).map(i => db.users.insert(createUser(i)))
        val usersFromDb = UserManager.all
        
        usersFromDb.map(_.id) === (1 to 5)
      }
    }
    
    "allow creation of users" in new WithDatabase {
      transaction {
        db.drop
        db.create
        
        UserManager.all.length === 0
        UserManager.createUser("user1", "mail1", "pass1")
        UserManager.all.length === 1
        UserManager.createUser("user2", "mail2", "pass2")
        UserManager.all.length === 2
      
        UserManager.all.map(_.id) === (1 to 2)
      }
    }
    
    "grant users with valid credentials" in new WithDatabase {
      transaction {
        db.drop
        db.create

        db.users.insert(new User(0L, "user1", "mail1", "pass1", "", "", "", ""))
        db.users.insert(new User(0L, "user2", "mail2", "pass2", "", "", "", ""))

        UserManager.authenticate("user1", "pass1").get.id === 1
        UserManager.authenticate("user2", "pass2").get.id === 2
        UserManager.authenticate("UsEr1", "pass1").get.id === 1
        UserManager.authenticate("uSeR2", "pass2").get.id === 2
      }
    }
    
    "deny users with invalid credentials" in new WithDatabase {
      transaction {
        db.drop
        db.create

        db.users.insert(new User(0L, "user1", "mail1", "pass1", "", "", "", ""))
        db.users.insert(new User(0L, "user2", "mail2", "pass2", "", "", "", ""))
      
        UserManager.authenticate("unknown", "pass") === None
        UserManager.authenticate("user1", "wrong") === None
        UserManager.authenticate("user2", "wrong") === None
        UserManager.authenticate("user1", "pass2") === None
        UserManager.authenticate("user2", "pass1") === None
      }
    }
  }
}
