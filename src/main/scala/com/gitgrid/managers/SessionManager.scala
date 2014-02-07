package com.gitgrid.managers

import java.security.SecureRandom
import spray.http.DateTime
import org.apache.commons.codec.binary.Base64

case class Session(id: String, userId: Long, expires: Option[DateTime])

trait SessionManager {
  def createSession(userId: Long, expires: Option[DateTime]): Session
  def loadSession(sessionId: String, now: Option[DateTime] = None): Option[Session]
  def cleanExpiredSessions(now: Option[DateTime]): Unit
}

class InMemorySessionManager extends SessionManager {
  private val random = new SecureRandom()
  val sessions = scala.collection.mutable.Map.empty[String, Session]

  def createSession(userId: Long, expires: Option[DateTime]): Session = {
    val sessionId = generateSessionId()
    val session = Session(sessionId, userId, expires)
    sessions(sessionId) = session

    session
  }

  def loadSession(sessionId: String, now: Option[DateTime] = None): Option[Session] = {
    if (sessions.contains(sessionId)) {
      val session = sessions(sessionId)

      if (!session.expires.isDefined || session.expires > now.orElse(Some(DateTime.now))) {
        Some(session)
      } else None
    }
    else None
  }

  def cleanExpiredSessions(now: Option[DateTime]): Unit = {
    val now2 = now.orElse(Some(DateTime.now)).get
    val expiredSessions = sessions.values.filter(s => s.expires match {
      case Some(dt) if dt < now2 => true
      case _ => false
    }).toList

    expiredSessions.foreach(s => sessions.remove(s.id))
  }

  private def generateSessionId(): String = {
    val bin = new Array[Byte](32)
    random.nextBytes(bin)
    val str = Base64.encodeBase64String(bin)

    str
  }
}
