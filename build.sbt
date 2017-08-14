name := """Play-Main-Assignment"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"


libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test
javaOptions in Test += "-Dconfig.file=conf/test.conf"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "org.postgresql" % "postgresql" % "42.1.4",

  specs2 % Test,
  evolutions
)
// https://mvnrepository.com/artifact/org.mindrot/jbcrypt
libraryDependencies += "org.mindrot" % "jbcrypt" % "0.3m"
libraryDependencies += "com.h2database" % "h2" % "1.4.188"
libraryDependencies += "org.mockito" % "mockito-all" % "1.9.5" % "test"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
