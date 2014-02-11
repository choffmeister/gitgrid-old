package com.gitgrid.webapi.directives

import scala.util.Success
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import spray.http._
import spray.routing._
import spray.routing.authentication._
import spray.routing.AuthenticationFailedRejection._
import spray.routing.Directives._
import com.gitgrid.managers.AuthManager
import com.gitgrid.managers.AuthenticationPass
import com.gitgrid.managers.Session
import com.gitgrid.mongodb.User

trait AuthDirectives {
  import com.gitgrid.webapi.JsonProtocol._

  implicit val authManager: AuthManager
  implicit val executor: ExecutionContext

  val cookieName = "gitgrid-sid"

  def authenticateOption: Directive1[Option[User]] = {
    extract(_.request.cookies.find(c => c.name == cookieName)).flatMap {
      case Some(cookie) =>
       	onComplete(authManager.loadSession(cookie.content)).map {
       	  case Success(Some(authPass)) => Some(authPass.user)
       	  case _ => None
       	}
      case _ => provide(None)
    }
  }

  def authenticate: Directive1[User] =
    authenticateOption.flatMap {
      case Some(u) => provide(u)
      case _ => reject(AuthenticationFailedRejection(CredentialsRejected, Nil))
    }

  def createAuthenticationCookie(session: Session): Directive0 =
    setCookie(HttpCookie(cookieName, session.id, expires = session.expires, path = Some("/")))

  def removeAuthenticationCookie: Directive0 =
    deleteCookie(cookieName, path = "/")
}
