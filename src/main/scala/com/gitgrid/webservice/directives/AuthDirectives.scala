package com.gitgrid.webservice.directives

import spray.http._
import spray.routing._
import spray.routing.authentication._
import spray.routing.AuthenticationFailedRejection._
import spray.routing.Directives._
import shapeless.HNil
import com.gitgrid.managers.AuthManager
import com.gitgrid.managers.AuthenticationPass
import com.gitgrid.managers.Session
import com.gitgrid.models.User

trait AuthDirectives {
  import com.gitgrid.webservice.JsonProtocol._

  val cookieName = "gitgrid-sid"

  def loginByCredentials(authManager: AuthManager): Directive1[Option[AuthenticationPass]] = {
    entity(as[UserPass]).flatMap {
      case UserPass(userName, password) =>
        authManager.authenticate(userName, password) match {
          case Some(AuthenticationPass(u, s)) => hprovide(Some(AuthenticationPass(u, s)) :: HNil)
          case _ => hprovide(None :: HNil)
        }
      case _ =>
        hprovide(None :: HNil)
    }
  }

  def checkAuthCookie(authManager: AuthManager): Directive1[Option[User]] = {
    extract { ctx =>
      val cookie = ctx.request.cookies.find(c => c.name == cookieName)
      cookie match {
        case Some(c) => authManager.loadSession(c.content) match {
          case Some(s) => Some(s.user)
          case _ => None
        }
        case _ => None
      }
    }
  }

  def ensureAuthCookie(authManager: AuthManager): Directive1[User] =
    checkAuthCookie(authManager).flatMap {
      case Some(u) => hprovide(u :: HNil)
      case _ => reject(AuthenticationFailedRejection(CredentialsRejected, Nil))
    }

  def createAuthCookie(authManager: AuthManager, session: Session): Directive0 =
    setCookie(HttpCookie(cookieName, session.id, expires = session.expires, path = Some("/")))

  def removeAuthCookie(authManager: AuthManager): Directive0 =
    deleteCookie(cookieName, path = "/")
}

object AuthDirectives extends AuthDirectives
