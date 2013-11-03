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

class WebServiceActor extends Actor with WebService {
  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(route)
}

abstract class RestResourceAction
case class List() extends RestResourceAction
case class Create() extends RestResourceAction
case class Retrieve(id: Long) extends RestResourceAction
case class Update(id: Long) extends RestResourceAction
case class Delete(id: Long) extends RestResourceAction

class RestActor extends Actor with ActorLogging {
  def receive = {
    case x =>
      log.info(x.toString())
      sender ! "Hello"
  }
}

trait WebService extends HttpService {
  import ExecutionContext.Implicits.global
  
  implicit val timeout = Timeout(5 seconds)
  
  def actorRefFactory: ActorContext

  def restRoute[A <: Actor : ClassTag, B : ClassTag](name: String): Route =
    path(name) {
      get {
        val actor = actorRefFactory.actorOf(Props[A])
        val future = actor ? List()
        complete(future.map(r => r.toString()))
        //complete(s"GET ${name}")
      } ~
      post {
        complete(s"POST ${name}")
      } 
    } ~
    pathPrefix(name) {
      path(LongNumber) { id =>
        get {
          complete(s"GET ${name}#${id}")
        } ~
        put {
          complete(s"PUT ${name}#${id}")
        } ~
        delete {
          complete(s"DELETE ${name}#${id}")
        }
      }
    }

  val route =
    pathPrefix("api") {
      path("ping") {
        get {
          respondWithMediaType(`text/html`) {
            complete {
              <html>
                <head>
                  <title>ping</title>
                </head>
                <body>
                  <h1>pong</h1>
                </body>
              </html>
            }
          }
        }
      } ~
      restRoute[RestActor, User]("users")
    }
}
