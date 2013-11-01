name := "asserthub"

version := "0.0.0"

version ~= { version => version + "-SNAPSHOT"}

scalaVersion := "2.10.3"

resolvers += "spray repo" at "http://repo.spray.io"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.1",
  "io.spray" % "spray-can" % "1.2-RC1",
  "org.squeryl" %% "squeryl" % "0.9.5-6",
  "junit" % "junit" % "4.11" % "test",
  "com.novocode" % "junit-interface" % "0.10" % "test",
  "com.h2database" % "h2" % "1.2.127" % "test"
)

packSettings

packMain := Map("asserthub" -> "de.choffmeister.asserthub.Application")

EclipseKeys.withSource := true
