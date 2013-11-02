name := "asserthub"

version := "0.0.0"

version ~= { version => version + "-SNAPSHOT"}

scalaVersion := "2.10.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io"
)

libraryDependencies ++= {
  val akkaVersion = "2.2.3"
  val sprayVersion = "1.2-RC2"
  Seq(
    "io.spray" % "spray-can" % sprayVersion,
    "io.spray" % "spray-testkit" % sprayVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    "org.squeryl" %% "squeryl" % "0.9.5-6",
    "junit" % "junit" % "4.11" % "test",
    "com.novocode" % "junit-interface" % "0.10" % "test",
    "com.h2database" % "h2" % "1.2.127" % "test"
  )
}

packSettings

packMain := Map("asserthub" -> "de.choffmeister.asserthub.Application")

EclipseKeys.withSource := true
