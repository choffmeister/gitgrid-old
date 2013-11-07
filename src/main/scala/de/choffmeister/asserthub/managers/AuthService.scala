package de.choffmeister.asserthub

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import de.choffmeister.asserthub.managers.AuthManager
import de.choffmeister.asserthub.managers.AuthenticationPass
import spray.http.BasicHttpCredentials
import spray.http.HttpChallenge
import spray.http.HttpCredentials
import spray.http.HttpHeaders.`WWW-Authenticate`
import spray.http.HttpRequest
import spray.routing.RequestContext
import spray.routing.authentication.HttpAuthenticator

class AuthService(implicit val authManager: AuthManager, implicit val executionContext: ExecutionContext) extends HttpAuthenticator[AuthenticationPass] {
  val realm = "asserthub"
  
  def authenticate(credentials: Option[HttpCredentials], ctx: RequestContext): Future[Option[AuthenticationPass]] = {
    val authPass = credentials.flatMap {
      case BasicHttpCredentials(user, pass) => authManager.authenticate(user, pass)
      case _ => None
    }
    
    Future.successful(authPass)
  }

  def getChallengeHeaders(httpRequest: HttpRequest) = {
    `WWW-Authenticate`(HttpChallenge(scheme = "Basic", realm = realm, params = Map.empty)) :: Nil
  }
}