package de.choffmeister.asserthub.managers

import org.junit.Assert._
import org.junit._
import org.squeryl.PrimitiveTypeMode._
import de.choffmeister.asserthub._

class UserManagerTest extends DatabaseAwareTest {
  @Test def test() {
    def createUser(i: Int) = new User(s"user${i}", s"user${i}@invalid.domain.tld", s"First${i}", s"Last${i}")
    
    transaction {
      Database.drop
      Database.create

      val users = (1 to 5).map(i => Database.users.insert(createUser(i)))
      val usersFromDb = UserManager.allUsers
    
      assertEquals(1 to 5, usersFromDb.map(_.id))
    }
  }
}
