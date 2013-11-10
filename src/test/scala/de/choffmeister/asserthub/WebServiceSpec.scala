package de.choffmeister.asserthub

import org.specs2.mutable._
import de.choffmeister.asserthub.models._
import de.choffmeister.asserthub.models.Dsl.transaction
import de.choffmeister.asserthub.JsonProtocol._
import spray.testkit._
import spray.http._
import spray.routing._
import spray.routing.authentication.UserPass
import spray.http.parser.HttpParser
import StatusCodes._

class WebServiceSpec extends SpecificationWithJUnit with Specs2RouteTest with WebService {
  def actorRefFactory = system

  "WebService" should {
    "accept valid login credentials" in new WithDatabase {
      transaction {
        db.drop
        db.create
        val users = (1 to 5).map(i => db.users.insert(createUser(i)))

        Post("/api/auth/login", UserPass("user1", "pass1")) ~> route ~> check {
          val res = responseAs[AuthenticationResponse]

          status === OK
          res.user.get.id === 1
          res.user.get.userName == "user1"
          headers.find(h => h.name.toLowerCase == "set-cookie") must beSome
        }

        Post("/api/auth/login", UserPass("user2", "pass2")) ~> route ~> check {
          val res = responseAs[AuthenticationResponse]

          status === OK
          res.user.get.id === 2
          res.user.get.userName == "user2"
          headers.find(h => h.name.toLowerCase == "set-cookie") must beSome
        }
      }
    }

    "reject invalid login credentials" in new WithDatabase {
      transaction {
        db.drop
        db.create
        val users = (1 to 5).map(i => db.users.insert(createUser(i)))

        Post("/api/auth/login") ~> route ~> check {
          rejection
        }

        Post("/api/auth/login", UserPass("user1", "pass2")) ~> route ~> check {
          rejection must beAnInstanceOf[AuthenticationFailedRejection]
        }

        Post("/api/auth/login", UserPass("user2", "pass1")) ~> route ~> check {
          rejection must beAnInstanceOf[AuthenticationFailedRejection]
        }

        Post("/api/auth/login", UserPass("unknown", "pass")) ~> route ~> check {
          rejection must beAnInstanceOf[AuthenticationFailedRejection]
        }
      }
    }

    "handle auth logout requests" in {
      Post("/api/auth/logout") ~> route ~> check {
        val res = responseAs[AuthenticationResponse]

        res.user must beNone
      }
    }

    "handle auth state requests" in new WithDatabase{
      transaction {
        db.drop
        db.create
        val users = (1 to 5).map(i => Database.users.insert(createUser(i)))
        var sessionId = ""

        Get("/api/auth/state") ~> route ~> check {
          rejection must beAnInstanceOf[AuthenticationFailedRejection]
        }

        Post("/api/auth/login", UserPass("user1", "pass1")) ~> route ~> check {
          val res = responseAs[AuthenticationResponse]
          val setCookieHeader = headers.find(h => h.name.toLowerCase == "set-cookie").get
          val cookie = setCookieHeader.asInstanceOf[HttpHeaders.`Set-Cookie`].cookie

          status === OK
          sessionId = cookie.content
        }

        Get("/api/auth/state") ~> addHeader(HttpHeaders.Cookie(HttpCookie("asserthub-sid", sessionId))) ~> route ~> check {
          val res = responseAs[User]

          status === OK
        }
      }
    }
  }

  def createUser(i: Long) = new User(0L, s"user${i}", s"user${i}@invalid.domain.tld", s"pass${i}", "", "plain", s"First${i}", s"Last${i}")
}
