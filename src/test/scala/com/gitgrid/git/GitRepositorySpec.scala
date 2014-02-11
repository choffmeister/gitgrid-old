package com.gitgrid.git

import org.specs2.mutable._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import spray.json._
import com.gitgrid.WithTemporaryDirectory
import com.gitgrid.util.ZipHelper

@RunWith(classOf[JUnitRunner])
class GitRepositorySpec extends Specification {
  "create repository" in new WithTemporaryDirectory(false) {
    GitRepository.init(directory, true)
    ok
  }

  "allow reading in repository" in new WithTemporaryDirectory(false) {
    ZipHelper.unzip(classOf[GitRepositorySpec].getResourceAsStream("/gitignore.zip"), directory)

    GitRepository(directory) { repo =>
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
