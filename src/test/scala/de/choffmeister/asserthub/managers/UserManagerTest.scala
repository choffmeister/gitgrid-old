package de.choffmeister.asserthub.managers

import org.junit.Assert._
import org.junit._
import de.choffmeister.asserthub.models._
import de.choffmeister.asserthub.models.Dsl._
import de.choffmeister.asserthub.DatabaseAwareTest

class UserManagerTest extends DatabaseAwareTest {
  @Test def testAllUsers() {
    def createUser(i: Int) = new User(0L, s"user${i}", s"user${i}@invalid.domain.tld", s"First${i}", s"Last${i}")
    
    transaction {
      Database.drop
      Database.create

      val users = (1 to 5).map(i => Database.users.insert(createUser(i)))
      val usersFromDb = UserManager.all
    
      assertEquals(1 to 5, usersFromDb.map(_.id))
    }
  }
  
  @Test def testCreateUser() {
    transaction {
      Database.drop
      Database.create
      
      assertEquals(0, UserManager.all.length)
      UserManager.createUser("user1", "mail1", "pass1")
      assertEquals(1, UserManager.all.length)
      UserManager.createUser("user2", "mail2", "pass2")
      assertEquals(2, UserManager.all.length)
      
      assertEquals(1 to 2, UserManager.all.map(_.id))
    }
  }
  
  @Test def testAuthenticate() {
    transaction {
      Database.drop
      Database.create

      val user1 = Database.users.insert(new User(0L, "user1", "mail1", "pass1", "", "", "", ""))
      val user2 = Database.users.insert(new User(0L, "user2", "mail2", "pass2", "", "", "", ""))
      
      assertNotEquals(0L, user1.id)
      assertNotEquals(0L, user2.id)
      
      assertEquals(None, UserManager.authenticate("unknown", "pass"))
      assertEquals(None, UserManager.authenticate("user1", "wrong"))
      assertEquals(None, UserManager.authenticate("user2", "wrong"))
      assertEquals(None, UserManager.authenticate("user1", "pass2"))
      assertEquals(None, UserManager.authenticate("user2", "pass1"))
      assertEquals(user1.id, UserManager.authenticate("user1", "pass1").get.id)
      assertEquals(user2.id, UserManager.authenticate("user2", "pass2").get.id)
      assertEquals(user1.id, UserManager.authenticate("UsEr1", "pass1").get.id)
      assertEquals(user2.id, UserManager.authenticate("uSeR2", "pass2").get.id)
    }
  }
}
