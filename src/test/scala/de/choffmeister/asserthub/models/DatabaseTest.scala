package de.choffmeister.asserthub.models

import org.junit.Assert._
import org.junit._
import de.choffmeister.asserthub.models.Dsl._
import de.choffmeister.asserthub.DatabaseAwareTest

class DatabaseTest extends DatabaseAwareTest {
  @Test def test() {
    def createUser(i: Int) = new User(s"user${i}", s"user${i}@invalid.domain.tld", s"First${i}", s"Last${i}")
    
    transaction {
      Database.drop
      Database.create

      val users = (1 to 5).map(i => Database.users.insert(createUser(i)))

      // assert that the return User objects have set their id properly
      assertEquals(1 to 5, users.map(_.id))
        
      // assert that the users can be fetched from the database
      assertEquals((1 to 5).map(i => s"user${i}"), (1 to 5).map(i => Database.users.where(u => u.id === i).single.userName))
    }
  }
}
