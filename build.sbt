lazy val root = Project("elastic4s", file("."))
  .settings(publish := {})
  .settings(publishArtifact := false)
  .settings(name := "elastic4s")
  .aggregate(
    core,
    testkit,
    jackson
  )

lazy val core = Project("elastic4s-core", file("elastic4s-core"))
  .settings(
    name := "elastic4s-core",
    libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % JacksonVersion % "test",
    libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion % "test",
    libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonVersion % "test" exclude("org.scala-lang", "scala-library")
  )

lazy val testkit = Project("elastic4s-testkit", file("elastic4s-testkit"))
  .settings(name := "elastic4s-testkit")
  .dependsOn(core)

lazy val jackson = Project("elastic4s-jackson", file("elastic4s-jackson"))
  .settings(
    name := "elastic4s-jackson",
    libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % JacksonVersion,
    libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion,
    libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonVersion exclude("org.scala-lang", "scala-library")
  ).dependsOn(core, testkit % "test")
