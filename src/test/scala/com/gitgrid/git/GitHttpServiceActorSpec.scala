package com.gitgrid.git

import com.gitgrid.testutils._
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class GitHttpServiceActorSpec extends Specification with ExternalToolSpec with RunApplicationSpec {
  sequential

  "allow clone with native git application" in new WithTemporaryDirectory(true) {
    if (!checkExecute(Seq("git", "--version"))) skipped("Command git is not in PATH")

    runApplication { app =>
      execute(Seq("git", "clone", "http://localhost:8080/user1/project1.git"), Some(directory))
      execute(Seq("git", "clone", "http://localhost:8080/user2/project2.git"), Some(directory))
      execute(Seq("git", "clone", "http://localhost:8080/user0/project0.git"), Some(directory)) must throwA[ExternalToolException]

      ok
    }
  }
}
