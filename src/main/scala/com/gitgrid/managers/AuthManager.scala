package com.gitgrid.managers

import com.gitgrid.models.User
import com.gitgrid.webapi.JsonProtocol._
import spray.http.DateTime

case class AuthenticationPass(user: User, session: Session)

class AuthManager(sessionManager: SessionManager) {
  def authenticate(userName: String, password: String): Option[AuthenticationPass] = {
    UserManager.authenticate(userName, password) match {
      case Some(u) =>
        val session = sessionManager.createSession(u.id, Some(DateTime.now + (30 * 60 * 1000)))
        Some(AuthenticationPass(u, session))
      case _ => None
    }
  }

  def loadSession(sessionId: String, now: Option[DateTime] = None): Option[AuthenticationPass] = {
    sessionManager.loadSession(sessionId, now) match {
      case Some(session) =>
        val user = UserManager.find(session.userId)
        user match {
          case Some(u) => Some(AuthenticationPass(u, session))
          case _ => None
        }
      case _ => None
    }
  }

  def cleanExpiredSessions(now: Option[DateTime]): Unit = {
    sessionManager.cleanExpiredSessions(now)
  }
}
