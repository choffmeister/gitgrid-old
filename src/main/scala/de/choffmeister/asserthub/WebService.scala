package de.choffmeister.asserthub

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import akka.actor.Actor
import akka.actor.ActorContext
import akka.actor.ActorLogging
import akka.pattern.ask
import akka.util.Timeout
import de.choffmeister.asserthub.JsonProtocol._
import de.choffmeister.asserthub.managers._
import de.choffmeister.asserthub.models._
import shapeless.HNil
import spray.http._
import spray.http.MediaTypes._
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport._
import spray.routing._
import spray.routing.AuthenticationFailedRejection._
import spray.routing.authentication.HttpAuthenticator

class WebServiceActor extends Actor with WebService {
  def actorRefFactory = context
  def receive = runRoute(route)
}

trait WebService extends HttpService {
  implicit val timeout = Timeout(5 seconds)
  implicit def executionContext = actorRefFactory.dispatcher
  implicit val authManager = new AuthManager()
  implicit val authService = new AuthService()

  val route =
    pathPrefix("api") {
      pathPrefix("auth") {
        path("login") {
          post {
            authenticate[AuthenticationPass](authService) { pass =>
              setCookie(HttpCookie("asserthub-sid", pass.session.id, pass.session.expires)) {
                complete {
                  pass.user
                }
              }
            }
          }
        } ~
        path("logout") {
          post {
            deleteCookie("asserthub-sid") {
              complete("logout")
            }
          }
        } ~
        path("state") {
          authManager.authCookieForce { user =>
            get {
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
