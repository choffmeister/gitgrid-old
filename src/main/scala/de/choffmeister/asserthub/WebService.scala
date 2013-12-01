package de.choffmeister.asserthub

import scala.concurrent.duration._
import scala.util.matching.Regex
import akka.actor._
import akka.util.Timeout
import de.choffmeister.asserthub.JsonProtocol._
import de.choffmeister.asserthub.managers._
import de.choffmeister.asserthub.models._
import spray.http._
import spray.http.StatusCodes._
import spray.routing._
import spray.httpx.unmarshalling.Unmarshaller
import spray.httpx.unmarshalling.Deserializer
import spray.http.Uri.Path

case class AuthenticationResponse(message: String, user: Option[User])

class WebServiceActor extends Actor with WebService {
  def actorRefFactory = context
  def receive = runRoute(route)
}

trait WebService extends HttpService {
  implicit val timeout = Timeout(5 seconds)
  implicit def executionContext = actorRefFactory.dispatcher

  def staticContentPathMapper(path: String): Option[String] = path match {
    case path if !path.startsWith("api/") =>
      val extensionRegex = "\\.([^/\\.]+)$".r
      val extension = extensionRegex.findFirstIn(path)

      extension match {
        case Some(ext) => Some(path)
        case _ => Some("index.html")
      }
    case _ => None
  }

  val staticContentPathMatcher: PathMatcher1[String] = Rest flatMap(staticContentPathMapper)

  val route =
    pathPrefix("api") {
      pathPrefix("auth") {
        path("login") {
          post {
            AuthManager.global.authLogin { pass =>
              pass match {
                case Some(AuthenticationPass(u, s)) =>
                  setCookie(HttpCookie("asserthub-sid", s.id, expires = s.expires, path = Some("/"))) {
                    complete(AuthenticationResponse("Logged in", Some(u)))
                  }
                case _ =>
                  complete(AuthenticationResponse("Invalid credentials", None))
              }
            }
          }
        } ~
        path("logout") {
          post {
            deleteCookie("asserthub-sid", path = "/") {
              complete(AuthenticationResponse("Logged out", None))
            }
          }
        } ~
        path("state") {
          get {
            AuthManager.global.authCookie { user =>
              user match {
                case Some(u) => complete(AuthenticationResponse("Valid session", Some(u)))
                case None => complete(AuthenticationResponse("No or invalid session", None))
              }
            }
          }
        }
      } ~
      CrudRoute.create("users", UserManager) ~
      CrudRoute.create("projects", ProjectManager) ~
      CrudRoute.create("tickets", TicketManager)
    } ~
    path(staticContentPathMatcher) { filePath =>
      getFromResource("web/" + filePath)
    }
}
