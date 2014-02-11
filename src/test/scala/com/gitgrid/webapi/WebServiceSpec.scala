package com.gitgrid.webapi

import org.specs2.mutable._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import com.gitgrid.webapi.JsonProtocol._
import com.gitgrid.mongodb._
import com.gitgrid.WithPreparedDatabase
import spray.testkit._
import spray.http._
import spray.routing._
import spray.routing.authentication.UserPass
import spray.http.StatusCodes._
import akka.actor._

class WebServiceImpl(actorRefFactory2: => ActorRefFactory) extends WebApiService {
  def actorRefFactory = actorRefFactory2
}

@RunWith(classOf[JUnitRunner])
class WebServiceSpec extends Specification with Specs2RouteTest {
  def webService = new WebServiceImpl(system)
  def sealRoute = webService.sealRoute _
  val route = webService.route

  sequential

  "WebService" should {
    "accept valid login credentials" in new WithPreparedDatabase {
      Post("/api/auth/login", UserPass("user1", "pass1")) ~> route ~> check {
        val res = responseAs[AuthenticationResponse]

        status === OK
        res.user.get.userName == "user1"
        headers.find(h => h.name.toLowerCase == "set-cookie") must beSome
      }

      Post("/api/auth/login", UserPass("user2", "pass2")) ~> route ~> check {
        val res = responseAs[AuthenticationResponse]

        status === OK
        res.user.get.userName == "user2"
        headers.find(h => h.name.toLowerCase == "set-cookie") must beSome
      }
    }

    "reject invalid login credentials" in new WithPreparedDatabase {
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

    "handle auth logout requests" in new WithPreparedDatabase {
      Post("/api/auth/logout") ~> route ~> check {
        val res = responseAs[AuthenticationResponse]

        res.user must beNone
      }
    }

    "handle auth state requests" in new WithPreparedDatabase {
      Get("/api/auth/state") ~> route ~> check {
        val res = responseAs[AuthenticationResponse]

        status === OK
        res.user must beNone
      }

      var sessionId = ""

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
}
