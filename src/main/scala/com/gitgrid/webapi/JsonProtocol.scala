package com.gitgrid.webapi

import com.gitgrid.models._
import spray.httpx._
import spray.json._
import spray.routing.authentication.UserPass
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}
import org.eclipse.jgit.revwalk._
import org.eclipse.jgit.lib._
import com.gitgrid.util._
import com.gitgrid.git._

object JsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit object DateFormat extends JsonFormat[Date] {
    def write(date: Date) = {
      JsNumber(date.getTime)
    }
    def read(value: JsValue) =
      value match {
        case JsNumber(time) =>
          try {
            new Date(time.toLong)
          } catch {
            case e: Throwable => deserializationError("Timestamp in seconds since 1970-01-01 00:00:00 UTC expected. Got " + value, e)
          }
        case _ => deserializationError("Timestamp in seconds since 1970-01-01 00:00:00 UTC expected. Got " + value)
      }
  }

  implicit object TimestampFormat extends JsonFormat[Timestamp] {
    def write(timestamp: Timestamp) = {
      JsNumber(timestamp.getTime)
    }
    def read(value: JsValue) =
      value match {
        case JsNumber(time) =>
          try {
            new Timestamp(time.toLong)
          } catch {
            case e: Throwable => deserializationError("Timestamp in seconds since 1970-01-01 00:00:00 UTC expected. Got " + value, e)
          }
        case _ => deserializationError("Timestamp in seconds since 1970-01-01 00:00:00 UTC expected. Got " + value)
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

  implicit val gitRefFormat = jsonFormat2(GitRef)
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
