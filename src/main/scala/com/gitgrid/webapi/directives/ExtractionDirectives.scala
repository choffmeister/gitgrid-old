package com.gitgrid.webapi.directives

import com.gitgrid.mongodb._
import scala.concurrent._
import spray.routing._
import spray.routing.Directives._

trait ExtractionDirectives {
  implicit val executor: ExecutionContext

  def projectPathPrefix: Directive1[Option[Project]] = {
    pathPrefix("projects" / Segment / Segment).flatMap { (userName, projectCanonicalName) =>
      onSuccess(Projects.findByFullQualifiedName(userName, projectCanonicalName)) {
        case Some(project) => provide(Some(project))
        case _ => provide(None)
      }
    }
  }
}
