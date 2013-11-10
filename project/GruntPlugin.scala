import sbt._
import Keys._

object GruntPlugin extends Plugin {
  val grunt = taskKey[Unit]("executes grunt with target 'default'")
  val gruntTest = taskKey[Unit]("executes grunt with target 'test'")
  val gruntDist = taskKey[Unit]("executes grunt with target 'prod-build'")
  val gruntStart = taskKey[Unit]("starts a background grunt process")
  val gruntStop = taskKey[Unit]("stops running backgrund grunt process")
  val gruntWebDir = settingKey[File]("grunt-webdir")

  lazy val gruntSettings = Seq[Def.Setting[_]](
    gruntWebDir := baseDirectory.value / "src/web",

    grunt := {
      val webDir: File = gruntWebDir.value

      runGrunt(webDir, "default")
    },

    gruntTest := {
      val webDir: File = gruntWebDir.value

      runGrunt(webDir, "test")
    },

    gruntDist := {
      val webDir: File = gruntWebDir.value

      runGrunt(webDir, "prod-build")
    },

    gruntStart := {
      val webDir: File = gruntWebDir.value

      startGrunt(webDir, "default")
    },

    gruntStop := {
      val webDir: File = gruntWebDir.value

      stopGrunt()
    }
  )

  private def runGrunt(cwd: File, task: String) = {
    val command = "grunt" :: task :: Nil
    val returnValue = Process(command, cwd) !

    if (returnValue != 0) {
      throw new Exception(s"Grunt task $task failed")
    }
  }

  private def startGrunt(cwd: File, task: String) = {
    if (running) {
      stopGrunt()
    }

    process = Process("grunt" :: "--force" :: task :: Nil, cwd).run()
    running = true
  }

  private def stopGrunt() = {
    process.destroy()
    running = true
  }

  private var running: Boolean = false
  private var process: Process = _
}
