import sbt._
import Keys._

// put into build.sbt: (compile in Compile) <<= (compile in Compile) dependsOn (gruntTask("prod-build"))
object GruntPlugin extends Plugin {
  private var running: Boolean = false
  private var process: Process = _

  // make project root relative
  private lazy val webDir = new java.io.File("src/web")

  private def runGrunt(task: String) = {
    val command = "grunt" :: task :: Nil
    val returnValue = Process(command, webDir) !

    if (returnValue != 0) {
      throw new Exception(s"Grunt task $task failed")
    }
  }

  private def startGrunt(task: String) = {
    if (running) {
      stopGrunt()
    }

    process = Process("grunt" :: "--force" :: task :: Nil, webDir).run()
    running = true
  }

  private def stopGrunt() = {
    process.destroy()
    running = true
  }

  def gruntCommand = {
    Command.command("grunt") { (state: State) =>
      runGrunt("default")

      state
    }
  }

  def gruntTaskCommand = {
    Command.single("gruntTask") { (state: State, task: String) =>
      runGrunt(task)

      state
    }
  }

  def gruntTestCommand = {
    Command.command("gruntTest") { (state: State) =>
      runGrunt("test")

      state
    }
  }

  def gruntStartCommand = {
    Command.command("gruntStart") { (state: State) =>
      startGrunt("default")

      state
    }
  }

  def gruntStopCommand = {
    Command.command("gruntStop") { (state: State) =>
      stopGrunt()

      state
    }
  }

  def gruntTask(task: String) = streams map { (s: TaskStreams) =>
    runGrunt(task)
  }

  override lazy val settings = Seq(commands ++= Seq(gruntCommand, gruntTaskCommand, gruntTestCommand, gruntStartCommand, gruntStopCommand))
}
