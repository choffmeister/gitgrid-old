package com.gitgrid.models

import org.specs2.mutable.SpecificationWithJUnit
import com.gitgrid.models._
import com.gitgrid.models.Dsl.transaction
import com.gitgrid.WithDatabase

class DatabaseSpec extends SpecificationWithJUnit {
  def createUser(i: Int) = new User(0L, s"user${i}", s"user${i}@invalid.domain.tld", "pass")

  "Database" should {
    "allow interaction with database" in new WithDatabase {
      transaction {
        import com.gitgrid.models.Dsl._

        db.drop
        db.create

        val users = (1 to 5).map(i => db.users.insert(createUser(i)))

        users.map(_.id) === (1 to 5)

        (1 to 5).map(i => db.users.lookup(i.toLong).get.userName) === (1 to 5).map(i => s"user${i}")
      }
    }
  }
}
