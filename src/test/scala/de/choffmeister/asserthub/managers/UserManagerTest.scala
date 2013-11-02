package de.choffmeister.asserthub.managers

import org.junit.Assert._
import org.junit._
import org.squeryl.PrimitiveTypeMode._
import de.choffmeister.asserthub._

class UserManagerTest extends DatabaseAwareTest {
  @Test def testAllUsers() {
    def createUser(i: Int) = new User(s"user${i}", s"user${i}@invalid.domain.tld", s"First${i}", s"Last${i}")
    
    transaction {
      Database.drop
      Database.create

      val users = (1 to 5).map(i => Database.users.insert(createUser(i)))
      val usersFromDb = UserManager.allUsers
    
      assertEquals(1 to 5, usersFromDb.map(_.id))
    }
  }
  
  @Test def testAuthenticate() {
    transaction {
      Database.drop
      Database.create

      val user1 = Database.users.insert(new User(0L, "user1", "mail1", "pass1", "", "", "", ""))
      val user2 = Database.users.insert(new User(0L, "user2", "mail2", "pass2", "", "", "", ""))
      
      assertEquals(None, UserManager.authenticate("unknown", "pass"))
      assertEquals(None, UserManager.authenticate("user1", "wrong"))
      assertEquals(None, UserManager.authenticate("user2", "wrong"))
      assertEquals(None, UserManager.authenticate("user1", "pass2"))
      assertEquals(None, UserManager.authenticate("user2", "pass1"))
      assertEquals(Some(user1), UserManager.authenticate("user1", "pass1"))
      assertEquals(Some(user2), UserManager.authenticate("user2", "pass2"))
      assertEquals(Some(user1), UserManager.authenticate("UsEr1", "pass1"))
      assertEquals(Some(user2), UserManager.authenticate("uSeR2", "pass2"))
    }
  }
}
