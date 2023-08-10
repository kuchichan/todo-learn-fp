ThisBuild / scalaVersion := "3.2.0"
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / githubWorkflowPublishTargetBranches := Seq()

val commonSettings = Seq(
  name := "todo-app",
  scalacOptions -= "-Xfatal-warnings",
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-effect" % "3.5.1",
    "org.typelevel" %% "cats-mtl" % "1.3.0",
    "org.typelevel" %% "munit-cats-effect-3" % "1.0.7",
  ),

)

lazy val root = project.in(file("."))
  .settings(publish := {}, publish / skip := true, commonSettings)
