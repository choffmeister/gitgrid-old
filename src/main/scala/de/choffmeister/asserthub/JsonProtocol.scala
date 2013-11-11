package de.choffmeister.asserthub

import de.choffmeister.asserthub.models._
import spray.httpx._
import spray.json._
import spray.routing.authentication.UserPass
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

object JsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit object TimestampFormat extends RootJsonFormat[Timestamp] {
    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")

    def write(ts: Timestamp) =
      JsString(format.format(new Date(ts.getTime())))

    def read(value: JsValue) =
      value match {
        case JsString(s) => new Timestamp(format.parse(s).getTime())
        case _ => deserializationError("Timestamp expected: " + value)
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
}
