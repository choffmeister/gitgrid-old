package de.choffmeister.asserthub

import de.choffmeister.asserthub.models.User
import spray.json._

object JsonProtocol extends DefaultJsonProtocol {
  implicit object UserJsonFormat extends RootJsonFormat[User] {
    def write(u: User) =
      JsArray(JsNumber(u.id), JsString(u.userName), JsString(u.email), JsString(u.firstName), JsString(u.lastName))

    def read(value: JsValue) = value match {
      case JsArray(JsNumber(id) :: JsString(userName) :: JsString(email) :: JsString(firstName) :: JsString(lastName) :: Nil) =>
        new User(id.toLong, userName, email, firstName, lastName, "", "", "")
        
      case _ => deserializationError("User expected: " + value)
    }
  }
}