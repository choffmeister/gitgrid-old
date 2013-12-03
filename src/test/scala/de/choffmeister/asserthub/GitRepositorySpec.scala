package de.choffmeister.asserthub

import org.specs2.mutable._
import spray.json._
import java.io.File

class GitRepositorySpec extends SpecificationWithJUnit {
  val dir = new File("/home/choffmeister/Development/asserthub/.git")

  "work" in {
    GitRepository(dir) { repo =>
      val commitId = repo.resolve("master")
      val commit = repo.commit(commitId)
      val tree = repo.tree(commit.tree)
      val blob = repo.blob(tree.entries.find(_.name == "build.sbt").get.id)

      repo.traverse(commit, "/build.sbt") must beAnInstanceOf[GitBlob]
      repo.traverse(commit, "/build.sbt") === blob

      repo.traverse(commit, "/") must beAnInstanceOf[GitTree]
      repo.traverse(commit, "/") === tree
      repo.traverse(commit, "/src") must beAnInstanceOf[GitTree]
      repo.traverse(commit, "/src/main") must beAnInstanceOf[GitTree]
      repo.traverse(commit, "/src/main/scala/de/choffmeister/asserthub/Application.scala") must beAnInstanceOf[GitBlob]

      ok
    }
  }
}