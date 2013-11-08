package de.choffmeister.asserthub

import scala.concurrent.duration._
import akka.actor._
import akka.util.Timeout
import de.choffmeister.asserthub.JsonProtocol._
import de.choffmeister.asserthub.managers._
import de.choffmeister.asserthub.models._
import spray.http._
import spray.http.StatusCodes._
import spray.routing._

case class AuthenticationResponse(message: String, user: Option[User])

class WebServiceActor extends Actor with WebService {
  def actorRefFactory = context
  def receive = runRoute(route)
}

trait WebService extends HttpService {
  implicit val timeout = Timeout(5 seconds)
  implicit def executionContext = actorRefFactory.dispatcher
  implicit val authManager = new AuthManager()

  val route =
    pathPrefix("api") {
      pathPrefix("auth") {
        path("login") {
          post {
            authManager.authLogin { pass =>
              setCookie(HttpCookie("asserthub-sid", pass.session.id, pass.session.expires)) {
                complete(AuthenticationResponse("Logged in", Some(pass.user)))
              }
            }
          }
        } ~
        path("logout") {
          post {
            deleteCookie("asserthub-sid") {
              complete(AuthenticationResponse("Logged out", None))
            }
          }
        } ~
        path("state") {
          get {
            authManager.authCookieForce { user =>
              complete(user)
            }
          }
        }
      } ~
      createRestRoutes("users")
    }

  def createRestRoutes(name: String): Route = {
    val list = path(name) & get
    val retrieve = path(name / LongNumber) & get
    val create = path(name) & post
    val update = path(name / LongNumber) & put
    val remove = path(name / LongNumber) & delete
    
    list {
      complete {
        val users = UserManager.all
        users
      }
    } ~
    retrieve { id =>
      complete {
        val user = UserManager.find(id)
        user
      }
    } ~
    create { 
      entity(as[User]) { user =>
        complete {
          val persistedUser = UserManager.insert(user)
          persistedUser
        }
      }
    } ~
    update { id =>
      entity(as[User]) { user =>
        complete {
          if (user.id == id) {
            UserManager.update(user)
            user
          } else BadRequest
        }
      }
    } ~
    remove { id =>
      complete {
        UserManager.delete(id)
        "Delete #" + id
      }
    }
  }
}
