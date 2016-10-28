lazy val root = Project("elastic4s", file("."))
  .settings(publish := {})
  .settings(publishArtifact := false)
  .settings(name := "elastic4s")
  .aggregate(
    core,
    testkit,
    circe,
    jackson,
    json4s,
    playjson,
    streams
  )

lazy val core = Project("elastic4s-core", file("elastic4s-core"))
  .settings(name := "elastic4s-core")

lazy val testkit = Project("elastic4s-testkit", file("elastic4s-testkit"))
  .settings(
    name := "elastic4s-testkit",
    libraryDependencies ++= Seq(
      "org.scalatest"       %% "scalatest"    % ScalatestVersion,
      "org.elasticsearch"   % "elasticsearch" % ElasticsearchVersion
    )
  )
  .dependsOn(core)

lazy val coreTests = Project("elastic4s-core-tests", file("elastic4s-core-tests"))
  .settings(name := "elastic4s-core-tests")
  .settings(
    libraryDependencies += "org.scalatest" %% "scalatest" % ScalatestVersion % "test",
    libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % JacksonVersion % "test",
    libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion % "test",
    libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonVersion % "test" exclude("org.scala-lang", "scala-library")
  )
  .dependsOn(core, testkit % "test")

lazy val streams = Project("elastic4s-streams", file("elastic4s-streams"))
  .settings(
    name := "elastic4s-streams",
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
    libraryDependencies += "org.reactivestreams" % "reactive-streams" % ReactiveStreamsVersion,
    libraryDependencies += "org.reactivestreams" % "reactive-streams-tck" % ReactiveStreamsVersion % "test"
  ).dependsOn(core, testkit % "test", jackson % "test")

lazy val jackson = Project("elastic4s-jackson", file("elastic4s-jackson"))
  .settings(
    name := "elastic4s-jackson",
    libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % JacksonVersion,
    libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion,
    libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonVersion exclude("org.scala-lang", "scala-library"),
    libraryDependencies += "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % JacksonVersion
  ).dependsOn(core, testkit % "test")

lazy val circe = Project("elastic4s-circe", file("elastic4s-circe"))
.settings(
  name := "elastic4s-circe",
  libraryDependencies += "io.circe" %% "circe-core" % CirceVersion,
  libraryDependencies +=  "io.circe" %% "circe-generic" % CirceVersion,
  libraryDependencies +=  "io.circe" %% "circe-parser" % CirceVersion
).dependsOn(core, testkit % "test")

lazy val json4s = Project("elastic4s-json4s", file("elastic4s-json4s"))
  .settings(
    name := "elastic4s-json4s",
    libraryDependencies += "org.json4s" %% "json4s-core" % "3.2.11",
    libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.2.11"
  ).dependsOn(core, testkit % "test")

lazy val playjson = Project("elastic4s-play-json", file("elastic4s-play-json"))
    .settings(
      name := "elastic4s-play-json",
      libraryDependencies += "com.typesafe.play" %% "play-json" % PlayJsonVersion
    ).dependsOn(core, testkit % "test")
