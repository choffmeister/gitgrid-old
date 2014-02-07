package com.gitgrid.managers

import org.specs2.mutable._

import com.gitgrid.WithDatabase
import com.gitgrid.models.Dsl.transaction
import spray.http.DateTime

class AuthManagerSpec extends SpecificationWithJUnit {
  val now = DateTime(2013, 1, 1, 12, 0, 0)

  "AuthManager" should {
    "authenticate" in new WithDatabase {
      transaction {
        db.drop
        db.create

        val sm = new InMemorySessionManager()
        val auth = new AuthManager(sm)
        val user1 = UserManager.createUser("user1", "user1@test.com", "pass1", "plain")
        val user2 = UserManager.createUser("user2", "user2@test.com", "pass2", "plain")

        auth.authenticate("user1", "wrong") must beNone
        auth.authenticate("unknown", "pass1") must beNone
        auth.authenticate("user1", "pass2") must beNone
        auth.authenticate("user2", "pass1") must beNone
        auth.loadSession("someWrongSessionKey") must beNone

        val pass1 = auth.authenticate("user1", "pass1")
        pass1 must beSome
        pass1.get.user.id === 1

        val pass2 = auth.authenticate("user2", "pass2")
        pass2 must beSome
        pass2.get.user.id === 2
      }
    }
  }
}
