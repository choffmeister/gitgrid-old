name := "asserthub"

version := "0.0.0"

version ~= { version => version + "-SNAPSHOT"}

scalaVersion := "2.10.3"

scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-language:postfixOps", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io"
)

libraryDependencies ++= {
  val akkaVersion = "2.2.3"
  val sprayVersion = "1.2-RC4"
  Seq(
    "ch.qos.logback" % "logback-classic" % "1.0.13",
    "com.h2database" % "h2" % "1.2.127",
    "com.jcraft" % "jsch" % "0.1.50",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "commons-codec" % "commons-codec" % "1.8",
    "io.spray" % "spray-can" % sprayVersion,
    "io.spray" % "spray-routing" % sprayVersion,
    "io.spray" % "spray-testkit" % sprayVersion % "test",
    "io.spray" %% "spray-json" % "1.2.5",
    "joda-time" % "joda-time" % "2.3",
    "junit" % "junit" % "4.11" % "test",
    "mysql" % "mysql-connector-java" % "5.1.12",
    "org.eclipse.jgit" % "org.eclipse.jgit" % "3.1.0.201310021548-r",
    "org.specs2" %% "specs2" % "2.2.3" % "test",
    "org.squeryl" %% "squeryl" % "0.9.6-RC1"
  )
}

testOptions in Test += Tests.Argument("junitxml", "console")

ScctPlugin.instrumentSettings

CoveragePlugin.coverageSettings

packSettings

packMain := Map("asserthub" -> "de.choffmeister.asserthub.Application")

gruntSettings

packExtraClasspath := Map("asserthub" -> Seq("${PROG_HOME}/res"))

pack <<= pack dependsOn(gruntDist)

pack <<= (baseDirectory, pack, streams) map { (baseDirectory: File, value: File, s) =>
  val gruntProdTargetDir = baseDirectory / "target/web/prod"
  val gruntProdPackDir = baseDirectory / "target/pack/res/web"
  s.log.info("Copying web files to target/pack/res/web")
  IO.delete(gruntProdPackDir)
  gruntProdPackDir.mkdirs()
  IO.copyDirectory(gruntProdTargetDir, gruntProdPackDir)
  s.log.info("done.")
  value
}

EclipseKeys.withSource := true
