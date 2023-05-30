ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "rm2",
    idePackagePrefix := Some("eu.alpacaislands.rm2")
  )

mainClass := Some("eu.alpacaislands.rm2.Main")


//builds
enablePlugins(
  JavaAppPackaging,
  LinuxPlugin,
  DebianPlugin,
  RpmPlugin,
  UniversalPlugin)

name := "rm2"

version := "0.1.0-SNAPSHOT"

maintainer := "Jens van de Wiel <jens.vdwiel@gmail.com>"

packageSummary := "Improved rm tool"

packageDescription := """Makes the default rm tool more safe"""
rpmVendor := "typesafe"


//debian
debianPackageDependencies := Seq("java8-runtime-headless")

//libraries

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.1.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % Test
libraryDependencies += "org.scalatestplus" %% "mockito-3-4" % "3.2.9.0" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "3.12.4" % Test
