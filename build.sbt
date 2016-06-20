name := """play-with-akka-2.4.5"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, PlayAkkaHttpServer)
  .disablePlugins(PlayNettyServer)

scalaVersion := "2.11.7"

val akkaVersion = "2.4.5"

libraryDependencies ++= Seq(
  ws,
  "com.typesafe.akka" %% "akka-http-core" % akkaVersion,  
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
