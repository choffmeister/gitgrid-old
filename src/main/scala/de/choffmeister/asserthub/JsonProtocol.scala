package de.choffmeister.asserthub

import de.choffmeister.asserthub.models._
import spray.httpx._
import spray.json._
import spray.routing.authentication.UserPass
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}
import org.joda.time.format.ISODateTimeFormat
import org.eclipse.jgit.revwalk._
import org.eclipse.jgit.lib._
import de.choffmeister.asserthub.util.GitTreeObjectType
import de.choffmeister.asserthub.util.GitTreeEntry
import de.choffmeister.asserthub.util.GitTree
import de.choffmeister.asserthub.util.GitTagObjectType
import de.choffmeister.asserthub.util.GitObjectType
import de.choffmeister.asserthub.util.GitCommitSignature
import de.choffmeister.asserthub.util.GitCommitObjectType
import de.choffmeister.asserthub.util.GitCommit
import de.choffmeister.asserthub.util.GitBlobObjectType
import de.choffmeister.asserthub.util.GitBlob

object JsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit object DateFormat extends JsonFormat[Date] {
    val formatWrite = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    formatWrite.setTimeZone(TimeZone.getTimeZone("UTC"))
    val formatRead = ISODateTimeFormat.dateTimeParser()

    def write(d: Date) = {
      JsString(formatWrite.format(d.getTime) + "Z")
    }

    def read(value: JsValue) =
      value match {
        case JsString(s) =>
          try {
            new Date(formatRead.parseMillis(s))
          } catch {
            case e: Throwable => deserializationError("Timestamp in ISO8601 UTC format expected. Got " + value, e)
          }
        case _ => deserializationError("Timestamp in ISO8601 UTC format expected. Got " + value)
      }
  }

  implicit object TimestampFormat extends JsonFormat[Timestamp] {
    val formatWrite = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    formatWrite.setTimeZone(TimeZone.getTimeZone("UTC"))
    val formatRead = ISODateTimeFormat.dateTimeParser()

    def write(d: Timestamp) = {
      JsString(formatWrite.format(d.getTime))
    }

    def read(value: JsValue) =
      value match {
        case JsString(s) =>
          try {
            new Timestamp(formatRead.parseMillis(s))
          } catch {
            case e: Throwable => deserializationError("Timestamp in ISO8601 UTC format expected. Got " + value, e)
          }
        case _ => deserializationError("Timestamp in ISO8601 UTC format expected. Got " + value)
      }
  }

  implicit object UserJsonFormat extends RootJsonFormat[User] {
    def write(u: User) =
      JsObject(Map(
        "id" -> JsNumber(u.id),
        "userName" -> JsString(u.userName),
        "email" -> JsString(u.email),
        "firstName" -> JsString(u.firstName),
        "lastName" -> JsString(u.lastName)
      ))

    def read(value: JsValue) =
      value.asJsObject.getFields("id", "userName", "email", "firstName", "lastName") match {
        case Seq(JsNumber(id), JsString(userName), JsString(email), JsString(firstName), JsString(lastName)) =>
          new User(id.toLong, userName, email, firstName, lastName, "", "", "")
        case _ =>
          deserializationError("User expected: " + value)
      }
  }

  implicit val projectFormat = jsonFormat6(Project)
  implicit val ticketFormat = jsonFormat5(Ticket)
  implicit val userPassFormat = jsonFormat2(UserPass)
  implicit val authenticationResponseFormat = jsonFormat2(AuthenticationResponse)

  implicit val gitCommitSignatureFormat = jsonFormat4(GitCommitSignature)
  implicit val gitCommitFormat = jsonFormat7(GitCommit)
  implicit object GitObjectTypeFormat extends JsonFormat[GitObjectType] {
    def write(t: GitObjectType) =
      t match {
        case GitCommitObjectType => JsString("commit")
        case GitTreeObjectType => JsString("tree")
        case GitBlobObjectType => JsString("blob")
        case GitTagObjectType => JsString("tag")
        case _ => deserializationError("Unknown git object type")
      }

    def read(value: JsValue) = deserializationError("Deserialization of GitObjectType is not implemented")
  }
  implicit val gitTreeEntryFormat = jsonFormat4(GitTreeEntry)
  implicit val gitTreeFormat = jsonFormat2(GitTree)
  implicit val gitBlobFormat = jsonFormat1(GitBlob)
}
