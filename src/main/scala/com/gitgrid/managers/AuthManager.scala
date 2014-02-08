package com.gitgrid.managers

import com.gitgrid.mongodb._
import reactivemongo.bson._
import spray.http.DateTime
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

case class AuthenticationPass(user: User, session: Session)

class AuthManager(sessionManager: SessionManager)(implicit ec: ExecutionContext) {
  def checkCredentials(userName: String, password: String): Future[Option[User]] = {
    Users.findByUserName(userName).map {
      case Some(user) if user.passwordHash == password => Some(user)
      case _ => None
    }
  }

  def authenticate(userName: String, password: String): Future[Option[AuthenticationPass]] = {
    checkCredentials(userName, password).map {
      case Some(user) =>
        val session = sessionManager.createSession(user.id.get.stringify, Some(DateTime.now + (30 * 60 * 1000)))
        Some(AuthenticationPass(user, session))
      case _ => None
    }
  }

  def loadSession(sessionId: String, now: Option[DateTime] = None): Future[Option[AuthenticationPass]] = {
    sessionManager.loadSession(sessionId, now) match {
      case Some(session) =>
        Users.find(BSONObjectID(session.userId)).map {
          case Some(user) => Some(AuthenticationPass(user, session))
          case _ => None
        }
      case _ => Future(None)
    }
  }

  def cleanExpiredSessions(now: Option[DateTime]): Unit = {
    sessionManager.cleanExpiredSessions(now)
  }
}
