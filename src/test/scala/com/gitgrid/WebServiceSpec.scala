package com.gitgrid

import org.specs2.mutable._
import com.gitgrid.models._
import com.gitgrid.models.Dsl.transaction
import com.gitgrid.JsonProtocol._
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
          responseAs[AuthenticationResponse].user must beNone
        }

        Post("/api/auth/login", UserPass("user2", "pass1")) ~> route ~> check {
          responseAs[AuthenticationResponse].user must beNone
        }

        Post("/api/auth/login", UserPass("unknown", "pass")) ~> route ~> check {
          responseAs[AuthenticationResponse].user must beNone
        }

        Post("/api/auth/login") ~> sealRoute(route) ~> check {
          status === BadRequest
          headers.find(h => h.name.toLowerCase == "set-cookie") must beNone
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
          val res = responseAs[AuthenticationResponse]

          status === OK
          res.user must beNone
        }

        Post("/api/auth/login", UserPass("user1", "pass1")) ~> route ~> check {
          val res = responseAs[AuthenticationResponse]
          val setCookieHeader = headers.find(h => h.name.toLowerCase == "set-cookie").get
          val cookie = setCookieHeader.asInstanceOf[HttpHeaders.`Set-Cookie`].cookie

          status === OK
          sessionId = cookie.content
        }

        Get("/api/auth/state") ~> addHeader(HttpHeaders.Cookie(HttpCookie("gitgrid-sid", sessionId))) ~> route ~> check {
          val res = responseAs[AuthenticationResponse]

          status === OK
          res.user must beSome
        }
      }
    }

    "map non API requests to static HTML filenames" in {
      staticContentPathMapper("") === Some("index.html")
      staticContentPathMapper("index.html") === Some("index.html")
      staticContentPathMapper("about") === Some("index.html")
      staticContentPathMapper("api") === Some("index.html")
      staticContentPathMapper("foo/bar/test.it/route") === Some("index.html")
      staticContentPathMapper("styles/main.css") === Some("styles/main.css")

      staticContentPathMapper("api/") must beNone
      staticContentPathMapper("api/index.html") must beNone
      staticContentPathMapper("api/about") must beNone
      staticContentPathMapper("api/foo/bar/test.it/route") must beNone
      staticContentPathMapper("api/styles/main.css") must beNone
    }
  }

  def createUser(i: Long) = new User(0L, s"user${i}", s"user${i}@invalid.domain.tld", s"pass${i}", "", "plain", s"First${i}", s"Last${i}")
}
