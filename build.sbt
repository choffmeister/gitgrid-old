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
  val sprayVersion = "1.2-RC2"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "io.spray" % "spray-can" % sprayVersion,
    "io.spray" % "spray-routing" % sprayVersion,
    "io.spray" % "spray-testkit" % sprayVersion % "test",
    "io.spray" %% "spray-json" % "1.2.5",
    "org.squeryl" %% "squeryl" % "0.9.6-RC1",
    "com.h2database" % "h2" % "1.2.127",
    "mysql" % "mysql-connector-java" % "5.1.12",
    "junit" % "junit" % "4.11" % "test",
    "org.specs2" %% "specs2" % "2.2.3" % "test"
  )
}

packSettings

packMain := Map("asserthub" -> "de.choffmeister.asserthub.Application")

EclipseKeys.withSource := true
