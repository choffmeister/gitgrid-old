package de.choffmeister.asserthub.modules

import de.choffmeister.asserthub.JsonProtocol._
import spray.routing.Directive0
import spray.routing.Directive1
import spray.routing.Directives._
import spray.http._

case class GitHubDeliveryHeader(value: String) extends HttpHeader {
  val name = GitHubHeaders.Delivery.name
  val lowercaseName = name.toLowerCase
 def render[R <: Rendering](r: R): r.type = r ~~ name ~~ ':' ~~ ' ' ~~ value
}

case class GitHubEventHeader(value: String) extends HttpHeader {
  val name = GitHubHeaders.Event.name
  val lowercaseName = name.toLowerCase
  def render[R <: Rendering](r: R): r.type = r ~~ name ~~ ':' ~~ ' ' ~~ value
}

object GitHubHeaders {
  object Delivery {
    val name = "X-Github-Delivery"
    def apply(value: String) = GitHubDeliveryHeader(value)
  }
  
  object Event {
    val name = "X-Github-Event"
    def apply(name: String) = GitHubEventHeader(name)
  }
}

class GitHubModule extends Module {
  val name = "GitHub"
  val routePrefix = "github"
    
  lazy val route =
    request { req =>
    path("hook") {
      post {
        gitHubEvent("push") {
          println(req)
          gitHubPayload { payload =>
            complete {
              println("#### PUSH EVENT")
              println(payload)
              "Hello"
            }
          }
        }
      }
    }
    }
  
  def gitHubEvent(): Directive1[String] = headerValueByName(GitHubHeaders.Event.name)
  def gitHubEvent(event: String): Directive0 = headerValueByName(GitHubHeaders.Event.name).flatMap {
    case s: String if s == event => pass
    case _ => reject
  }
  def gitHubDelivery: Directive1[String] = headerValueByName(GitHubHeaders.Delivery.name)
  def gitHubPayload: Directive1[String] = formField("payload")
  def request = extract(ctx => ctx.request)
}
