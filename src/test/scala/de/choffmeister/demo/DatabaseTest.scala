package de.choffmeister.asserthub

import org.junit.Assert.assertEquals
import org.junit.Test
import org.squeryl.PrimitiveTypeMode.transaction
import org.squeryl.adapters.H2Adapter

class DatabaseTest {
  @Test def test() {
    Database.createFactory(new H2Adapter(), "org.h2.Driver", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")

    transaction {
      Database.create

      val users = for (i <- 1 to 5) yield
        Database.users.insert(new User("user" + i, "user" + i + "@invalid.domain.tld", "First" + i, "Last" + i))

      for (i <- 1 to 5)
        assertEquals(i, users(i - 1).id)
    }
  }
}
