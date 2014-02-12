package com.gitgrid.git

import org.specs2.mutable._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import com.gitgrid.testutils._

@RunWith(classOf[JUnitRunner])
class GitHttpServiceActorSpec extends Specification with ExternalToolSpec with RunApplicationSpec {
  sequential

  "allow clone with native git application" in new WithTemporaryDirectory(true) {
    if (!checkExecute(Seq("git", "--version"))) skipped("Command git is not in PATH")

    runApplication { app =>
      val (exitCode1, stdOut1) = execute(Seq("git", "clone", "http://localhost:8080/user1/project1.git"), Some(directory))
      exitCode1 === 0

      val (exitCode2, stdOut2) = execute(Seq("git", "clone", "http://localhost:8080/user2/project2.git"), Some(directory))
      exitCode2 === 0

      val (exitCode3, stdOut3) = execute(Seq("git", "clone", "http://localhost:8080/user0/project0.git"), Some(directory))
      exitCode3 === 128
    }
  }
}
