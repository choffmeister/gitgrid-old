package de.choffmeister.asserthub.modules

import de.choffmeister.asserthub.JsonProtocol._
import spray.json._
import spray.routing.Directive0
import spray.routing.Directive1
import spray.routing.Directives._
import spray.http._
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.json.RootJsonFormat

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

case class GitHubSignature(
  name: String,
  username: String,
  email: String
)

case class GitHubRepositoryOwner(
  name: String,
  email: String
)

case class GitHubRepository(
  id: Int,
  name: String,
  description: String,
  owner: GitHubRepositoryOwner,
  master_branch: String,
  `private`: Boolean,
  fork: Boolean,
  forks: Int,
  has_downloads: Boolean,
  has_issues: Boolean,
  has_wiki: Boolean,
  open_issues: Int,
  pushed_at: Int,
  size: Int,
  stargazers: Int,
  url: String,
  watchers: Int
)

case class GitHubCommit(
  id: String,
  message: String,
  author: GitHubSignature,
  committer: GitHubSignature,
  timestamp: String,
  distinct: Boolean,
  url: String,
  added: List[String],
  modified: List[String],
  removed: List[String]
)

case class GitHubPayload(
  after: String,
  before: String,
  commits: List[GitHubCommit],
  compare: String,
  created: Boolean,
  deleted: Boolean,
  forced: Boolean,
  head_commit: GitHubCommit,
  /* pusher */
  ref: String,
  repository: GitHubRepository
)

trait GitHubJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val ownerFormat = jsonFormat2(GitHubRepositoryOwner)
  implicit val repositoryFormat = jsonFormat17(GitHubRepository)
  implicit val signatureFormat = jsonFormat3(GitHubSignature)
  implicit val commitFormat = jsonFormat10(GitHubCommit)
  implicit val payloadFormat = jsonFormat10(GitHubPayload)
}

class GitHubModule extends Module with GitHubJsonProtocol {
  val name = "GitHub"
  val routePrefix = "github"
    
  lazy val route =
    path("hook") {
      post {
        gitHubEvent("push") {
          gitHubPayload { payload =>
            complete {
              payload.repository.id.toString
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
  def gitHubPayload: Directive1[GitHubPayload] = formField("payload").flatMap {
    case s: String => provide(s.asJson.convertTo[GitHubPayload])
    case _ => reject
  }
  def request = extract(ctx => ctx.request)
}
