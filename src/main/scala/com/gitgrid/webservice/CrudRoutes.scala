package com.gitgrid.webservice

import com.gitgrid.managers._
import com.gitgrid.mongodb._
import com.gitgrid.webservice.directives._
import spray.routing._
import spray.http.StatusCodes._
import spray.httpx.marshalling.ToResponseMarshaller
import spray.httpx.unmarshalling.FromRequestUnmarshaller
import scala.concurrent.ExecutionContext
import reactivemongo.bson._

class CrudRoutes[T <: Entity](name: String, repo: EntityRepository[T])(implicit
  val authManager: AuthManager,
  val executor: ExecutionContext,
  entityMarshaller: ToResponseMarshaller[T],
  entityListMarshaller: ToResponseMarshaller[List[T]],
  entityOptionMarshaller: ToResponseMarshaller[Option[T]],
  entityUnmarshaller: FromRequestUnmarshaller[T],
  entityListUnmarshaller: FromRequestUnmarshaller[List[T]],
  entityOptionUnmarshaller: FromRequestUnmarshaller[Option[T]]
) extends Directives with AuthDirectives with ODataDirectives {
  val list = path(name) & get
  val retrieve = path(name / Segment).map(id => BSONObjectID(id)) & get
  val create = path(name) & post
  val update = path(name / Segment).map(id => BSONObjectID(id)) & put
  val remove = path(name / Segment).map(id => BSONObjectID(id)) & delete

  val route =
    list {
      odata { query =>
        // TODO: respect OData paging parameters
        onSuccess(repo.all) { l =>
          complete(l)
        }
      }
    } ~
    retrieve { id =>
      onSuccess(repo.find(id)) {
        case Some(e) => complete(e)
        case _ => complete(NotFound)
      }
    } ~
    create {
      entity(as[T]) { e =>
        ensureAuthCookie { user =>
          onSuccess(repo.insert(e)) { le =>
            complete(e)
          }
        }
      }
    } ~
    update { id =>
      entity(as[T]) { e =>
        if (e.id == Some(id)) {
          ensureAuthCookie { user =>
            onSuccess(repo.update(e)) { le =>
              complete(e)
            }
          }
        } else complete(BadRequest)
      }
    } ~
    remove { id =>
      ensureAuthCookie { user =>
        onSuccess(repo.delete(id)) { le =>
          complete(OK)
        }
      }
    }
}
