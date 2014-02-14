name := "gitgrid"

version := "0.0.0"

version ~= { version => version + "-SNAPSHOT"}

scalaVersion := "2.10.3"

scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-language:postfixOps", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io",
  "typesafe repo" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= {
  val akkaVersion = "2.2.3"
  val sprayVersion = "1.2.0"
  Seq(
    "ch.qos.logback" % "logback-classic" % "1.0.13",
    "com.jcraft" % "jsch" % "0.1.50",
    "com.typesafe" % "config" % "1.2.0",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "commons-codec" % "commons-codec" % "1.9",
    "io.spray" % "spray-can" % sprayVersion,
    "io.spray" % "spray-routing" % sprayVersion,
    "io.spray" % "spray-testkit" % sprayVersion % "test",
    "io.spray" %% "spray-json" % "1.2.5",
    "junit" % "junit" % "4.11" % "test",
    "org.eclipse.jgit" % "org.eclipse.jgit" % "3.2.0.201312181205-r",
    "org.reactivemongo" %% "reactivemongo" % "0.10.0",
    "org.specs2" %% "specs2" % "2.2.3" % "test"
  )
}

testOptions in Test += Tests.Argument("junitxml", "console")

parallelExecution in Test := false

ScctPlugin.instrumentSettings

CoveragePlugin.coverageSettings

packSettings

packMain := Map("gitgrid" -> "com.gitgrid.Application")

gruntSettings

packExtraClasspath := Map("gitgrid" -> Seq("${PROG_HOME}/res", "${PROG_HOME}/conf"))

pack <<= pack dependsOn(gruntDist)

pack <<= (baseDirectory, pack, streams) map { (baseDirectory: File, value: File, s) =>
  val gruntProdTargetDir = baseDirectory / "target/web/prod"
  val gruntProdPackDir = baseDirectory / "target/pack/res/web"
  s.log.info("Copying web files to target/pack/res/web")
  IO.delete(gruntProdPackDir)
  gruntProdPackDir.mkdirs()
  IO.copyDirectory(gruntProdTargetDir, gruntProdPackDir)
  s.log.info("done.")
  s.log.info("Copying config files")
  val confSourceDir = baseDirectory / "src/main/resources"
  val confTargetDir = baseDirectory / "target/pack/conf"
  confTargetDir.mkdirs()
  IO.copyFile(confSourceDir / "application.conf.dist", confTargetDir / "application.conf")
  IO.copyFile(confSourceDir / "logback.xml.dist", confTargetDir / "logback.xml")
  s.log.info("done.")
  value
}

org.scalastyle.sbt.ScalastylePlugin.Settings

EclipseKeys.withSource := true
