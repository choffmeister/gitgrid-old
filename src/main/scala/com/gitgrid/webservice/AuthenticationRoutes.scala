package com.gitgrid.webservice

import com.gitgrid.managers._
import com.gitgrid.models._
import com.gitgrid.webservice.directives._
import spray.http._
import spray.routing._

case class AuthenticationResponse(message: String, user: Option[User])

object AuthenticationRoutes extends HttpServiceBase {
  import JsonProtocol._

  def route(implicit authManager: AuthManager) =
    path("login") {
      post {
        authLogin(authManager) { pass =>
          pass match {
            case Some(AuthenticationPass(u, s)) =>
              setCookie(HttpCookie("gitgrid-sid", s.id, expires = s.expires, path = Some("/"))) {
                complete(AuthenticationResponse("Logged in", Some(u)))
              }
            case _ =>
              complete(AuthenticationResponse("Invalid credentials", None))
          }
        }
      }
    } ~
    path("logout") {
      post {
        deleteCookie("gitgrid-sid", path = "/") {
          complete(AuthenticationResponse("Logged out", None))
        }
      }
    } ~
    path("state") {
      get {
        authCookie(authManager) { user =>
          user match {
            case Some(u) => complete(AuthenticationResponse("Valid session", Some(u)))
            case None => complete(AuthenticationResponse("Invalid session", None))
          }
        }
      }
    }
}
