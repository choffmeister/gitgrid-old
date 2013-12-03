package de.choffmeister.asserthub

import java.io._
import scala.collection.JavaConversions._
import spray.routing._
import spray.routing.Directives._
import de.choffmeister.asserthub.JsonProtocol._
import org.eclipse.jgit.storage.file._
import org.eclipse.jgit.internal.storage.pack._
import org.eclipse.jgit.lib._
import org.eclipse.jgit.revwalk._
import org.eclipse.jgit.treewalk._
import java.util.Date

object GitRoute {
  val dir = new File("/home/choffmeister/Development/asserthub/.git")

  lazy val route: Route =
    path("commit" / Segment) { refOrSha =>
      complete(GitRepository(dir)(repo => repo.commit(repo.resolve(refOrSha))))
    } ~
    path("tree" / Segment) { sha =>
      complete(GitRepository(dir)(repo => repo.tree(repo.resolve(sha))))
    } ~
    path("blob" / Segment) { sha =>
      complete(GitRepository(dir)(repo => repo.blob(repo.resolve(sha)).readAsString(repo)))
    } ~
    path("tree" / Segment / RestPath) { (refOrSha, path) =>
      complete {
        GitRepository(dir) { repo =>
          val commitId = repo.resolve(refOrSha)
          val commit = repo.commit(commitId)
          val tree = repo.traverse(commit, "/" + path).asInstanceOf[GitTree]
          tree
        }
      }
    } ~
    path("blob"/ Segment / RestPath) { (refOrSha, path) =>
      complete {
        GitRepository(dir) { repo =>
          val commitId = repo.resolve(refOrSha)
          val commit = repo.commit(commitId)
          val blob = repo.traverse(commit, "/" + path).asInstanceOf[GitBlob]
          blob.readAsString(repo)
        }
      }
    }
}
