package de.choffmeister.asserthub.routes

import java.io._
import scala.collection.JavaConversions._
import spray.routing._
import spray.routing.Directives._
import de.choffmeister.asserthub.JsonProtocol._
import de.choffmeister.asserthub.util._
import de.choffmeister.asserthub.Config
import org.eclipse.jgit.storage.file._
import org.eclipse.jgit.internal.storage.pack._
import org.eclipse.jgit.lib._
import org.eclipse.jgit.revwalk._
import org.eclipse.jgit.treewalk._
import java.util.Date

object GitRoutes {
  def create(projectId: Long): Route =
    path("commit" / Segment) { refOrSha =>
      complete(gitRepository(projectId)(repo => repo.commit(repo.resolve(refOrSha))))
    } ~
    path("tree" / Segment) { sha =>
      complete(gitRepository(projectId)(repo => repo.tree(repo.resolve(sha))))
    } ~
    path("blob" / Segment) { sha =>
      complete(gitRepository(projectId)(repo => repo.blob(repo.resolve(sha)).readAsString(repo)))
    } ~
    path("tree" / Segment / RestPath) { (refOrSha, path) =>
      complete {
        gitRepository(projectId) { repo =>
          val commitId = repo.resolve(refOrSha)
          val commit = repo.commit(commitId)
          val tree = repo.traverse(commit, "/" + path).asInstanceOf[GitTree]
          tree
        }
      }
    } ~
    path("blob"/ Segment / RestPath) { (refOrSha, path) =>
      complete {
        gitRepository(projectId) { repo =>
          val commitId = repo.resolve(refOrSha)
          val commit = repo.commit(commitId)
          val blob = repo.traverse(commit, "/" + path).asInstanceOf[GitBlob]
          blob.readAsString(repo)
        }
      }
    }

  private def gitRepository[T](projectId: Long)(inner: GitRepository => T): T =
    GitRepository(new File(Config.repositoriesDir, projectId.toString))(inner)
}
