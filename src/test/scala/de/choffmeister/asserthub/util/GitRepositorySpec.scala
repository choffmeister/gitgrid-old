package de.choffmeister.asserthub.util

import org.specs2.mutable._
import spray.json._
import java.io.File
import java.util.UUID

class GitRepositorySpec extends SpecificationWithJUnit {
  def tmp = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID.toString)

  "create repository" in {
    val dir = tmp
    GitRepository.init(dir, true)
    ok
  }

  "allow reading in repository" in {
    val dir = tmp
    ZipHelper.unzip(classOf[GitRepositorySpec].getResourceAsStream("/gitignore.zip"), dir)

    GitRepository(dir) { repo =>
      val commitId = repo.resolve("master")
      commitId === "7b684c28663c20a287d67b2879db83957391c23b"
      val commit = repo.commit(commitId)
      val tree = repo.tree(commit.tree)
      val blob = repo.blob(tree.entries.find(_.name == "Scala.gitignore").get.id)

      repo.traverse(commit, "/Scala.gitignore") must beAnInstanceOf[GitBlob]
      repo.traverse(commit, "/Scala.gitignore") === blob

      repo.traverse(commit, "/") must beAnInstanceOf[GitTree]
      repo.traverse(commit, "/") === tree
      repo.traverse(commit, "/Global") must beAnInstanceOf[GitTree]
      repo.traverse(commit, "/Global/README.md") must beAnInstanceOf[GitBlob]

      ok
    }
  }
}