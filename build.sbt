ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

lazy val root = (project in file("."))
  .settings(
    name := "udemy-akka-essentials"
  )

val akkaVersion = "2.6.20"
val scalatestVersion = "3.2.18"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % scalatestVersion,
  "org.slf4j" % "slf4j-api" % "1.7.32",  // SLF4J
  "ch.qos.logback" % "logback-classic" % "1.2.6"  // SLF4J imp Logback
)