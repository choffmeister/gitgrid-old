package de.choffmeister.asserthub

import de.choffmeister.asserthub.models.User
import spray.json._

object JsonProtocol extends DefaultJsonProtocol {
  implicit object UserJsonFormat extends RootJsonFormat[User] {
    def write(u: User) =
      JsObject(Map(
        "id" -> JsNumber(u.id),
        "userName" -> JsString(u.userName),
        "email" -> JsString(u.email),
        "firstName" -> JsString(u.firstName),
        "lastName" -> JsString(u.lastName)
      ))
          
      //JsArray(JsNumber(u.id), JsString(u.userName), JsString(u.email), JsString(u.firstName), JsString(u.lastName))

    def read(value: JsValue) =
      value.asJsObject.getFields("id", "userName", "email", "firstName", "lastName") match {
        case Seq(JsNumber(id), JsString(userName), JsString(email), JsString(firstName), JsString(lastName)) =>
          new User(id.toLong, userName, email, firstName, lastName, "", "", "")
        case _ =>
          deserializationError("User expected: " + value)
      }
  }
}