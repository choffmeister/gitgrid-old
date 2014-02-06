package com.gitgrid.managers

import org.specs2.mutable._

import com.gitgrid.WithDatabase
import com.gitgrid.models.Dsl.transaction
import spray.http.DateTime

class AuthManagerSpec extends SpecificationWithJUnit {
  val now = DateTime(2013, 1, 1, 12, 0, 0)

  "AuthManager" should {
    "create sessions" in {
      val auth = new AuthManager()
      val session = auth.createSession(1, Some(now))

      auth.sessions must havePair(session.id -> session)
    }

    "cleanup expired sessions" in {
      val auth = new AuthManager()
      val session1 = auth.createSession(1, Some(now - 1))
      val session2 = auth.createSession(2, Some(now))
      val session3 = auth.createSession(3, Some(now + 1))
      val session4 = auth.createSession(4, None)

      auth.cleanSessions(Some(now))

      auth.sessions.keys must not contain(session1.id)
      auth.sessions must havePair(session2.id, session2)
      auth.sessions must havePair(session3.id, session3)
      auth.sessions must havePair(session4.id, session4)
    }

    "load sessions" in new WithDatabase {
      transaction {
        db.drop
        db.create

        val auth = new AuthManager()
        val user1 = UserManager.createUser("user1", "user1@test.com", "pass1", "plain")
        val user2 = UserManager.createUser("user2", "user2@test.com", "pass2", "plain")

        val session1 = auth.createSession(user1.id, Some(now + 1000))
        val session2 = auth.createSession(user2.id, Some(now + 1000))

        val pass1 = auth.loadSession(session1.id, Some(now))
        pass1 must beSome
        pass1.get.user.id === 1

        val pass2 = auth.loadSession(session2.id, Some(now))
        pass2 must beSome
        pass2.get.user.id === 2

        val pass3 = auth.loadSession("unknown-id", Some(now))
        pass3 must beNone
      }
    }

    "ignore expired sessions" in new WithDatabase {
      transaction {
        db.drop
        db.create

        val auth = new AuthManager()
        val user1 = UserManager.createUser("user1", "user1@test.com", "pass1", "plain")
        val user2 = UserManager.createUser("user2", "user2@test.com", "pass2", "plain")
        val user3 = UserManager.createUser("user3", "user3@test.com", "pass3", "plain")

        val session1 = auth.createSession(user1.id, Some(now + 1000))
        val session2 = auth.createSession(user2.id, Some(now + 3000))
        val session3 = auth.createSession(user3.id, None)


        val pass1 = auth.loadSession(session1.id, Some(now + 2000))
        pass1 must beNone

        val pass2 = auth.loadSession(session2.id, Some(now + 2000))
        pass2 must beSome
        pass2.get.user.id === 2

        val pass3 = auth.loadSession(session3.id, Some(now + 1000000))
        pass3 must beSome
        pass3.get.user.id === 3
      }
    }

    "create random session IDs" in {
      val auth = new AuthManager()
      val picks = (1 to 5).map(i => auth.generateSessionId())

      picks.distinct must haveSize(5)
    }

    "authenticate" in new WithDatabase {
      transaction {
        db.drop
        db.create

        val auth = new AuthManager()
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
