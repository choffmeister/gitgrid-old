package com.gitgrid.webapi

import com.gitgrid.managers._
import com.gitgrid.mongodb._
import com.gitgrid.webapi.JsonProtocol._
import spray.http._
import spray.routing._
import spray.routing.authentication.UserPass
import scala.concurrent._

case class AuthenticationResponse(message: String, user: Option[User])

class AuthenticationRoutes(implicit val authManager: AuthManager, val executor: ExecutionContext) extends Directives {
  import JsonProtocol._

  def route =
    pathPrefix("auth") {
      path("login") {
        post {
          entity(as[UserPass]) { userPass =>
            onComplete(authManager.authenticate(userPass.user, userPass.pass)) {
              case scala.util.Success(Some(authPass)) =>
                createAuthenticationCookie(authPass.session) {
                  complete(AuthenticationResponse("Logged in", Some(authPass.user)))
                }
              case _ =>
                complete(AuthenticationResponse("Invalid credentials", None))
            }
          }
        }
      } ~
      path("logout") {
        post {
          removeAuthenticationCookie {
            complete(AuthenticationResponse("Logged out", None))
          }
        }
      } ~
      path("state") {
        get {
          authenticateOption { user =>
            user match {
              case Some(u) => complete(AuthenticationResponse("Valid session", Some(u)))
              case None => complete(AuthenticationResponse("Invalid session", None))
            }
          }
        }
      }
    }
}
