package com.gitgrid.managers

import org.specs2.mutable._
import spray.http.DateTime

class InMemorySessionManagerSpec extends SpecificationWithJUnit {
  val now = DateTime(2013, 1, 1, 12, 0, 0)

  "InMemorySessionManager" should {
    "create sessions" in {
      val sm = new InMemorySessionManager()
      val session = sm.createSession(1, Some(now))

      sm.sessions must havePair(session.id -> session)
    }

    "clean expired sessions" in {
      val sm = new InMemorySessionManager()
      val session1 = sm.createSession(1, Some(now - 1))
      val session2 = sm.createSession(2, Some(now))
      val session3 = sm.createSession(3, Some(now + 1))
      val session4 = sm.createSession(4, None)

      sm.cleanExpiredSessions(Some(now))

      sm.sessions.keys must not contain(session1.id)
      sm.sessions must havePair(session2.id, session2)
      sm.sessions must havePair(session3.id, session3)
      sm.sessions must havePair(session4.id, session4)
    }

    "loadSession sessions" in {
      val sm = new InMemorySessionManager()
      val session1 = sm.createSession(1, Some(now + 1000))
      val session2 = sm.createSession(2, Some(now + 1000))

      val pass1 = sm.loadSession(session1.id, Some(now))
      pass1 must beSome
      pass1.get.userId === 1

      val pass2 = sm.loadSession(session2.id, Some(now))
      pass2 must beSome
      pass2.get.userId === 2

      val pass3 = sm.loadSession("unknown-id", Some(now))
      pass3 must beNone
    }

    "ignore expired sessions" in {
      val sm = new InMemorySessionManager()

      val session1 = sm.createSession(1, Some(now + 1000))
      val session2 = sm.createSession(2, Some(now + 3000))
      val session3 = sm.createSession(3, None)

      val pass1 = sm.loadSession(session1.id, Some(now + 2000))
      pass1 must beNone

      val pass2 = sm.loadSession(session2.id, Some(now + 2000))
      pass2 must beSome
      pass2.get.userId === 2

      val pass3 = sm.loadSession(session3.id, Some(now + 1000000))
      pass3 must beSome
      pass3.get.userId === 3
    }
  }
}
