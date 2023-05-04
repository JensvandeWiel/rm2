ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "rm2",
    idePackagePrefix := Some("eu.alpacaislands.rm2")
  )

mainClass := Some("eu.alpacaislands.rm2.Main")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % Test
libraryDependencies += "org.scalatestplus" %% "mockito-3-4" % "3.2.9.0" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "3.12.4" % Test