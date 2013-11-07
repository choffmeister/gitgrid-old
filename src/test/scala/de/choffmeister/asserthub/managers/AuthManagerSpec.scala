package de.choffmeister.asserthub.managers

import org.specs2.mutable._
import spray.http.DateTime
import org.specs2.specification.Scope
import de.choffmeister.asserthub.WithDatabase
import de.choffmeister.asserthub.models.Dsl._

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
        auth.authenticate("someWrongSessionKey") must beNone

        val pass1 = auth.authenticate("user1", "pass1")
        pass1 must beSome
        pass1.get.user.id === 1
        
        val pass2 = auth.authenticate("user2", "pass2")
        pass2 must beSome
        pass2.get.user.id === 2
        
        val passSession1 = auth.authenticate(pass1.get.session.id)
        passSession1 must beSome
        passSession1.get.user.id === 1
        
        val passSession2 = auth.authenticate(pass2.get.session.id)
        passSession2 must beSome
        passSession2.get.user.id === 2
      }
    }
  }
}