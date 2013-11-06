package de.choffmeister.asserthub.models

import org.specs2.mutable.SpecificationWithJUnit
import de.choffmeister.asserthub.models._
import de.choffmeister.asserthub.models.Dsl.transaction
import de.choffmeister.asserthub.WithDatabase

class DatabaseSpec extends SpecificationWithJUnit {
  def createUser(i: Int) = new User(0L, s"user${i}", s"user${i}@invalid.domain.tld", "pass")
    
  "Database" should {
    "allow interaction with database" in new WithDatabase {
      transaction {
        import de.choffmeister.asserthub.models.Dsl._
      
        db.drop
        db.create

        val users = (1 to 5).map(i => db.users.insert(createUser(i)))

        users.map(_.id) === (1 to 5)
        
        (1 to 5).map(i => db.users.lookup(i.toLong).get.userName) === (1 to 5).map(i => s"user${i}") 
      }
    }
  } 
}