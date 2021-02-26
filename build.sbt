import Dependencies._

ThisBuild / scalaVersion     := "2.13.4"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "Doobie Transactional Tests",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "2.2.0",
      "com.zaxxer" % "HikariCP" % "3.4.1",
      "org.tpolecat" %% "doobie-core" % "0.9.0",
      "org.postgresql" % "postgresql" % "42.2.12",
      "org.scalameta" %% "munit" % "0.7.4" % Test,
      "com.dimafeng" %% "testcontainers-scala-postgresql" % "0.39.2" % Test,
      "com.dimafeng" %% "testcontainers-scala-munit" % "0.39.2" % Test,
      "org.typelevel" %% "munit-cats-effect-2" % "0.13.1" % Test
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )
