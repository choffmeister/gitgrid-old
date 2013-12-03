import sbt._
import Keys._

case class GruntBowerVersions(nodeVersion: Option[VersionString], npmVersion: Option[VersionString], gruntVersion: Option[VersionString], bowerVersion: Option[VersionString])

object GruntBowerPlugin extends Plugin {
  val grunt = taskKey[Unit]("executes grunt with target 'default'")
  val gruntTest = taskKey[Unit]("executes grunt with target 'test'")
  val gruntDist = taskKey[Unit]("executes grunt with target 'prod-build'")
  val gruntStart = taskKey[Unit]("starts a background grunt process")
  val gruntStop = taskKey[Unit]("stops running backgrund grunt process")

  val gruntBowerVersions = taskKey[GruntBowerVersions]("retrieves the versions of node, npm, grunt-cli and bower")
  val gruntBowerInit = taskKey[Unit]("checks for node, npm, grunt-cli and bower and installs node modules and bower components")
  val gruntBowerWebDir = settingKey[File]("grunt-webdir")

  lazy val gruntSettings = Seq[Def.Setting[_]](
    grunt := {
      val webDir: File = gruntBowerWebDir.value

      runGrunt(webDir, "default")
    },

    gruntTest := {
      val webDir: File = gruntBowerWebDir.value

      runGrunt(webDir, "test")
    },

    gruntDist := {
      val webDir: File = gruntBowerWebDir.value

      runGrunt(webDir, "prod-build")
    },

    gruntStart := {
      val webDir: File = gruntBowerWebDir.value

      startGrunt(webDir, "default")
    },

    gruntStop := {
      val webDir: File = gruntBowerWebDir.value

      stopGrunt()
    },

    gruntBowerVersions := {
      val node = VersionString("node --version" !!)
      val npm = VersionString("npm --version" !!)
      val grunt = VersionString("grunt --version" !!)
      val bower = VersionString("bower --version" !!)

      GruntBowerVersions(node, npm, grunt, bower)
    },

    gruntBowerInit := {
      gruntBowerVersions.value match {
        case GruntBowerVersions(Some(node), Some(npm), Some(grunt), Some(bower)) =>
          println("Versions:")
          println("- NodeJS " + node)
          println("- NPM " + npm)
          println("- Grunt-CLI " + grunt)
          println("- Bower " + bower)
        case GruntBowerVersions(None, _, _, _) =>
          throw new Exception("NodeJS is not installed. Please refer to http://nodejs.org/ for installation instructions.")
        case GruntBowerVersions(_, None, _, _) =>
          throw new Exception("NPM is not installed. Please refer to http://nodejs.org/ for installation instructions.")
        case GruntBowerVersions(_, _, None, _) =>
          throw new Exception("Grunt-CLI is not installed. Please execute 'npm install -g grunt-cli'.")
        case GruntBowerVersions(_, _, _, None) =>
          throw new Exception("Bower is not installed. Please execute 'npm install -g bower'.")
      }

      val webDir: File = gruntBowerWebDir.value
      npmInstall(webDir)
      bowerInstall(webDir)
    },

    gruntBowerWebDir := baseDirectory.value / "src/web"
  )

  private def npmInstall(cwd: File) {
    val command = "npm" :: "install" :: Nil
    val returnValue = Process(command, cwd) !

    if (returnValue != 0) {
      throw new Exception("Installing Node modules failed")
    }
  }

  private def bowerInstall(cwd: File) {
    val command = "bower" :: "install" :: Nil
    val returnValue = Process(command, cwd) !

    if (returnValue != 0) {
      throw new Exception("Installing Bower components failed")
    }
  }

  private def runGrunt(cwd: File, task: String) {
    val command = "grunt" :: task :: Nil
    val returnValue = Process(command, cwd) !

    if (returnValue != 0) {
      throw new Exception(s"Grunt task $task failed")
    }
  }

  private def startGrunt(cwd: File, task: String) {
    if (running) {
      stopGrunt()
    }

    process = Process("grunt" :: "--force" :: task :: Nil, cwd).run()
    running = true
  }

  private def stopGrunt() {
    process.destroy()
    running = true
  }

  private var running: Boolean = false
  private var process: Process = _
}
