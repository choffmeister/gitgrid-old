package com.gitgrid.mongodb

import com.gitgrid.mongodb._
import com.gitgrid.testutils._
import org.specs2.mutable._

class ProjectSpec extends Specification with AsyncUtils {
  sequential

  "Projects" should {
    "find by full qualified name" in new WithPreparedDatabase {
      val p1 = await(Projects.findByFullQualifiedName("user1", "project1"))
      p1 must beSome
      p1.get.canonicalName === "project1"

      val p2 = await(Projects.findByFullQualifiedName("user2", "project2"))
      p2 must beSome
      p2.get.canonicalName === "project2"

      val p3 = await(Projects.findByFullQualifiedName("user3", "project3"))
      p3 must beSome
      p3.get.canonicalName === "project3"

      await(Projects.findByFullQualifiedName("user1", "project2")) must beNone
      await(Projects.findByFullQualifiedName("user2", "project1")) must beNone
      await(Projects.findByFullQualifiedName("user2", "project3")) must beNone
      await(Projects.findByFullQualifiedName("user3", "project2")) must beNone
    }
  }
}
