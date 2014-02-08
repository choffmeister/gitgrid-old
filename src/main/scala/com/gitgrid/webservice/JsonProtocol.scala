package com.gitgrid.webservice

import com.gitgrid.git._
import com.gitgrid.mongodb._
import java.util.Date
import reactivemongo.bson.BSONObjectID
import spray.httpx._
import spray.json._
import spray.routing.authentication.UserPass

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

  implicit object BSONObjectIDFormat extends JsonFormat[BSONObjectID] {
    def write(id: BSONObjectID) = JsString(id.stringify)
    def read(value: JsValue) =
      value match {
        case JsString(str) => BSONObjectID(str)
        case _ => deserializationError("BSON ID expected: " + value)
      }
  }

  implicit val userJSONFormat = jsonFormat6(User)
  implicit val projectJSONFormat = jsonFormat5(Project)
  implicit val ticketJSONFormat = jsonFormat5(Ticket)

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
