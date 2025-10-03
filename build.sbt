import coursierapi.MavenRepository

ThisBuild / organization     := "t-academy"
ThisBuild / organizationName := "T-Bank"
ThisBuild / version          := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"
ThisBuild / scalafixDependencies += "org.typelevel"       %% "typelevel-scalafix" % "0.5.0"
ThisBuild / scalafixDependencies += "com.github.vovapolu" %% "scaluzzi"           % "0.1.23"
ThisBuild / semanticdbEnabled                             := true

lazy val root = project.in(file("."))
  .settings(
    name         := "hw2-hangman",
    scalaVersion := "2.13.16",
    libraryDependencies ++= List(
      "org.typelevel" %% "cats-effect"                   % "3.6.3",
      "org.scalatest" %% "scalatest"                     % "3.2.19" % Test,
      "org.typelevel" %% "cats-effect-testing-scalatest" % "1.7.0"  % Test
    ),
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, _)) => List(
            "-no-indent"
          )
        case _ => List.empty
      }
    }
  )
