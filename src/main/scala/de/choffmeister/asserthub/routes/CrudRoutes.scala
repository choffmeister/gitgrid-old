package de.choffmeister.asserthub.routes

import de.choffmeister.asserthub.models._
import de.choffmeister.asserthub.managers.AuthManager
import spray.routing._
import spray.routing.Directives._
import spray.http.StatusCodes._
import de.choffmeister.asserthub.models.Dsl.{get => _, _}

case class ODataParameters(top: Option[Int], skip: Option[Int])

object CrudRoutes {
  lazy val odata: Directive1[ODataParameters] = {
    def parseInt(str: Option[String]): Option[Int] = str match {
      case Some(s) => Some(s.toInt)
      case _ => None
    }
    Directives.parameterMap.flatMap {
      case m: Map[String, String] => provide(ODataParameters(
        parseInt(m.get("$top")),
        parseInt(m.get("$skip"))
      ))
      case _ => provide(ODataParameters(None, None))
    }
  }

  def create[T <: Entity](name: String, repo: EntityRepository[T],
    beforeCreate: Option[(T, User) => T] = None,
    beforeUpdate: Option[(T, User) => T] = None)(implicit
    entityMarshaller: spray.httpx.marshalling.ToResponseMarshaller[T],
    entityListMarshaller: spray.httpx.marshalling.ToResponseMarshaller[List[T]],
    entityOptionMarshaller: spray.httpx.marshalling.ToResponseMarshaller[Option[T]],
    entityUnmarshaller: spray.httpx.unmarshalling.FromRequestUnmarshaller[T],
    entityListUnmarshaller: spray.httpx.unmarshalling.FromRequestUnmarshaller[List[T]],
    entityOptionUnmarshaller: spray.httpx.unmarshalling.FromRequestUnmarshaller[Option[T]]
  ): Route = {
    val list = path(name) & get
    val retrieve = path(name / LongNumber) & get
    val create = path(name) & post
    val update = path(name / LongNumber) & put
    val remove = path(name / LongNumber) & delete

    list {
      odata { query =>
        complete {
          inTransaction {
            val base = from(repo.table)(e => select(e) orderBy(e.id asc))
            val paged = base.page(query.skip.orElse(Some(0)).get, query.top.orElse(Some(100)).get)

            paged.toList
          }
        }
      }
    } ~
    retrieve { id =>
      complete {
        repo.find(id) match {
          case Some(e) => e
          case _ => NotFound
        }
      }
    } ~
    create {
      entity(as[T]) { e =>
        AuthManager.global.authCookieForce { user =>
          beforeCreate match {
            case Some(f) => complete(repo.insert(f(e, user)))
            case None => complete(repo.insert(e))
          }
        }
      }
    } ~
    update { id =>
      entity(as[T]) { e =>
        if (e.id == id) {
          AuthManager.global.authCookieForce { user =>
            beforeUpdate match {
              case Some(f) => complete(repo.update(f(e, user)))
              case None => complete(repo.update(e))
            }
          }
        } else complete(BadRequest)
      }
    } ~
    remove { id =>
      AuthManager.global.authCookieForce { user =>
        complete {
          repo.delete(id) match {
            case Some(e) => e
            case _ => NotFound
          }
        }
      }
    }
  }
}
