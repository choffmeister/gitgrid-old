package de.choffmeister.asserthub.managers

import java.security.SecureRandom
import scala.collection.mutable.Map
import de.choffmeister.asserthub.models.User
import spray.http.DateTime
import org.apache.commons.codec.binary.Base64

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
  
  def authenticate(sessionId: String): Option[AuthenticationPass] = {
    if (sessions.contains(sessionId)) {
      val session = sessions(sessionId)
      val user = UserManager.find(session.userId)
      
      user match {
        case Some(u) => Some(AuthenticationPass(u, session))
        case _ => None
      }
    }
    else None
  }
  
  def createSession(userId: Long, expires: Option[DateTime]): Session = {
    val sessionId = generateSessionId()
    val session = Session(sessionId, userId, expires)
    sessions(sessionId) = session
    
    session
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
}