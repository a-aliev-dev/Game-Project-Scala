val scala3Version = "3.8.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "game-project",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
      "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
      "com.google.inject" % "guice" % "7.0.0"
    ),

    coverageExcludedPackages := "Main\\$package;view.*;model.score.FantasyRealmsScoreStrategy",
    coverageExcludedFiles := ".*Main.scala"
  )