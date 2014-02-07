package com.gitgrid.webservice

import com.gitgrid.models._
import com.gitgrid.managers._
import com.gitgrid.webservice.directives._
import spray.routing._
import spray.http.StatusCodes._
import com.gitgrid.models.Dsl.{get => _, _}

object CrudRoutes {
  def create[T <: Entity](name: String, repo: EntityRepository[T],
    beforeCreate: Option[(T, User) => T] = None,
    beforeUpdate: Option[(T, User) => T] = None)(implicit
    authManager: AuthManager,
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
        ensureAuthCookie(authManager) { user =>
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
          ensureAuthCookie(authManager) { user =>
            beforeUpdate match {
              case Some(f) => complete(repo.update(f(e, user)))
              case None => complete(repo.update(e))
            }
          }
        } else complete(BadRequest)
      }
    } ~
    remove { id =>
      ensureAuthCookie(authManager) { user =>
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
