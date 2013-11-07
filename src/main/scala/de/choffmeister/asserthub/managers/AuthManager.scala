package de.choffmeister.asserthub.managers

import java.security.SecureRandom
import scala.annotation.migration
import scala.collection.mutable.Map
import scala.math.Ordered.orderingToOrdered
import org.apache.commons.codec.binary.Base64
import de.choffmeister.asserthub.models.User
import spray.http.DateTime
import spray.routing.Directive1
import spray.routing._
import spray.routing.directives.BasicDirectives._
import spray.routing.directives.RouteDirectives._
import shapeless.HNil
import AuthenticationFailedRejection._

case class Session(id: String, userId: Long, expires: Option[DateTime])
case class AuthenticationPass(user: User, session: Session)

class AuthManager {
  private val random = new SecureRandom()
  val sessions = Map.empty[String, Session]
  
  def authenticate(userName: String, password: String): Option[AuthenticationPass] = {
    UserManager.authenticate(userName, password) match {
      case Some(u) =>
        val session = createSession(u.id, Some(DateTime.now + (30 * 60 * 1000)))
        Some(AuthenticationPass(u, session))
      case _ => None
    }
  }
  
  def createSession(userId: Long, expires: Option[DateTime]): Session = {
    val sessionId = generateSessionId()
    val session = Session(sessionId, userId, expires)
    sessions(sessionId) = session
    
    session
  }
  
  def loadSession(sessionId: String, now: Option[DateTime] = None): Option[AuthenticationPass] = {
    if (sessions.contains(sessionId)) {
      val session = sessions(sessionId)
      
      if (!session.expires.isDefined || session.expires > now.orElse(Some(DateTime.now))) {
        val user = UserManager.find(session.userId)
      
        user match {
          case Some(u) => Some(AuthenticationPass(u, session))
          case _ => None
        }
      } else None
    }
    else None
  }
  
  def cleanSessions(dt: Some[DateTime]): Unit = {
    val now = dt.orElse(Some(DateTime.now)).get
    val expiredSessions = sessions.values.filter(s => s.expires match {
      case Some(dt) if dt < now => true
      case _ => false
    }).toList
    
    expiredSessions.foreach(s => sessions.remove(s.id))
  }
  
  def generateSessionId(): String = {
    val bin = new Array[Byte](32)
    random.nextBytes(bin)
    val str = Base64.encodeBase64String(bin)
    
    str
  }
  
  val authCookie: Directive1[Option[User]] = {
    extract { ctx =>
      val cookie = ctx.request.cookies.find(c => c.name == "asserthub-sid")
      
      cookie match {
        case Some(c) => loadSession(c.content) match {
          case Some(s) => Some(s.user)
          case _ => None
        }
        case _ => None
      }
    }
  }
  
  val authCookieForce: Directive1[User] =
    authCookie.flatMap {
      case Some(u) => hprovide(u :: HNil)
      case _ => reject(AuthenticationFailedRejection(CredentialsRejected, Nil))
    }
}