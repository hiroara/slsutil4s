val json4s = "org.json4s" %% "json4s-native" % "3.5.0"

lazy val commonSettings = Seq(
  organization := "biz.newrope",
  version := "0.0.0-SNAPSHOT",
  scalaVersion := "2.11.8"
)

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    name := "slsutil4s",
    libraryDependencies += json4s,
    assemblyJarName in assembly := "slsutil4s.jar"
  )

