package de.choffmeister.asserthub

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import akka.pattern.ask
import spray.routing._
import spray.http._
import MediaTypes._
import de.choffmeister.asserthub.managers.UserManager
import de.choffmeister.asserthub.models.User
import akka.actor.ActorContext
import scala.reflect.ClassTag
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import de.choffmeister.asserthub.models.User
import spray.json._
import de.choffmeister.asserthub.JsonProtocol._
import spray.httpx.SprayJsonSupport._
import StatusCodes._
import de.choffmeister.asserthub.managers.AuthManager
import AuthenticationFailedRejection._
import de.choffmeister.asserthub.managers.AuthenticationPass
import shapeless.HNil
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import spray.http.BasicHttpCredentials
import spray.http.HttpChallenge
import spray.http.HttpCredentials
import spray.http.HttpHeaders.`WWW-Authenticate`
import spray.http.HttpRequest
import spray.routing.RequestContext
import spray.routing.authentication.HttpAuthenticator
import spray.routing.authentication.UserPass
import spray.http.HttpCookie
import spray.http.DateTime
import spray.routing.Directive1

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
