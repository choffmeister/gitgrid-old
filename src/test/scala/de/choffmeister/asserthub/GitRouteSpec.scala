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

class GitRouteSpec extends SpecificationWithJUnit with Specs2RouteTest with WebService {
  def actorRefFactory = system

  "GitRoute" should {
    "serve commits" in {
      Get("/api/git/commit/master") ~> route ~> check {
        val res = responseAs[String]

        status === OK
        println(res)
        ok
      }

      Get("/api/git/commit/db74d9efda4979a02457a1b57437e232a203d8db") ~> route ~> check {
        val res = responseAs[String]

        status === OK
        println(res)
        ok
      }
    }

    "serve trees" in {
      Get("/api/git/tree/master/") ~> route ~> check {
        val res = responseAs[String]

        status === OK
        println(res)
          ok
      }

      Get("/api/git/tree/master/src/") ~> route ~> check {
        val res = responseAs[String]

        status === OK
        println(res)
        ok
      }
    }
  }
}
