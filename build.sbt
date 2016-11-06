lazy val root = Project("elastic4s", file("."))
  .settings(publish := {})
  .settings(publishArtifact := false)
  .settings(name := "elastic4s")
  .aggregate(
    core,
    embedded,
    testkit,
    coreTests,
    circe,
    jackson,
    json4s,
    playjson,
    streams
  )

lazy val core = Project("elastic4s-core", file("elastic4s-core"))
  .settings(
    name := "elastic4s-core",
    libraryDependencies ++= Seq(
      "org.scalatest"                         %% "scalatest"                % ScalatestVersion,
      "org.apache.logging.log4j"              % "log4j-api"                 % Log4jVersion % "test",
      "org.apache.logging.log4j"              % "log4j-core"                % Log4jVersion % "test",
      "org.apache.logging.log4j"              % "log4j-1.2-api"             % Log4jVersion % "test",
      "org.apache.logging.log4j"              % "log4j-slf4j-impl"          % "2.7"
    )
  )

lazy val embedded = Project("elastic4s-embedded", file("elastic4s-embedded"))
  .settings(
    name := "elastic4s-embedded",
    libraryDependencies ++= Seq(
      "org.elasticsearch"                     % "elasticsearch"             % ElasticsearchVersion,
      "com.fasterxml.jackson.dataformat"      % "jackson-dataformat-smile"  % "2.8.4",
      "org.apache.lucene"                     % "lucene-core"               % "6.2.1",
      "org.apache.lucene"                     % "lucene-analyzers-common"   % "6.2.1",
      "org.apache.lucene"                     % "lucene-backward-codecs"    % "6.2.1",
      "org.apache.lucene"                     % "lucene-grouping"           % "6.2.1",
      "org.apache.lucene"                     % "lucene-highlighter"        % "6.2.1",
      "org.apache.lucene"                     % "lucene-join"               % "6.2.1",
      "org.apache.lucene"                     % "lucene-memory"             % "6.2.1",
      "org.apache.lucene"                     % "lucene-misc"               % "6.2.1",
      "org.apache.lucene"                     % "lucene-queries"            % "6.2.1",
      "org.apache.lucene"                     % "lucene-queryparser"        % "6.2.1",
      "org.apache.lucene"                     % "lucene-sandbox"            % "6.2.1",
      "org.apache.lucene"                     % "lucene-spatial"            % "6.2.1",
      "org.apache.lucene"                     % "lucene-spatial-extras"     % "6.2.1",
      "org.apache.lucene"                     % "lucene-spatial3d"          % "6.2.1",
      "org.apache.lucene"                     % "lucene-suggest"            % "6.2.1",
      "com.fasterxml.jackson.dataformat"      % "jackson-dataformat-cbor"   % JacksonVersion,
      "org.apache.logging.log4j"              % "log4j-api"                 % Log4jVersion,
      "org.apache.logging.log4j"              % "log4j-core"                % Log4jVersion,
      "org.apache.logging.log4j"              % "log4j-1.2-api"             % Log4jVersion,
      "org.apache.logging.log4j"              % "log4j-slf4j-impl"          % Log4jVersion
    )
  )
  .dependsOn(core)

lazy val testkit = Project("elastic4s-testkit", file("elastic4s-testkit"))
  .settings(
    name := "elastic4s-testkit",
    libraryDependencies ++= Seq(
      "org.scalatest"                         %% "scalatest"                % ScalatestVersion
    )
  )
  .dependsOn(core, embedded)

lazy val coreTests = Project("elastic4s-core-tests", file("elastic4s-core-tests"))
  .settings(
    name := "elastic4s-core-tests",
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.core"    % "jackson-core"            % JacksonVersion % "test",
      "com.fasterxml.jackson.core"    % "jackson-databind"        % JacksonVersion % "test",
      "com.fasterxml.jackson.module"  %% "jackson-module-scala"   % JacksonVersion % "test" exclude("org.scala-lang", "scala-library")
    )
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
  libraryDependencies += "io.circe" %% "circe-core"     % CirceVersion,
  libraryDependencies +=  "io.circe" %% "circe-generic" % CirceVersion,
  libraryDependencies +=  "io.circe" %% "circe-parser"  % CirceVersion
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
