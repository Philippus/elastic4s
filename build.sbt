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
    streams,
    xpacksecurity
  )

lazy val core = Project("elastic4s-core", file("elastic4s-core"))
  .settings(name := "elastic4s-core")

lazy val xpacksecurity = Project("elastic4s-xpack-security", file("elastic4s-xpack-security"))
  .settings(
    name := "elastic4s-xpack-security",
    resolvers += "elastic" at "https://artifacts.elastic.co/maven",
    libraryDependencies += "org.elasticsearch.client" % "x-pack-transport" % ElasticsearchVersion
  ).dependsOn(core, testkit % "test")

lazy val embedded = Project("elastic4s-embedded", file("elastic4s-embedded"))
  .settings(
    name := "elastic4s-embedded",
    libraryDependencies ++= Seq(
      "org.elasticsearch"                     % "elasticsearch"             % ElasticsearchVersion,
      "com.fasterxml.jackson.dataformat"      % "jackson-dataformat-smile"  % JacksonVersion,
      "com.fasterxml.jackson.dataformat"      % "jackson-dataformat-cbor"   % JacksonVersion,
      "org.apache.logging.log4j"              % "log4j-api"                 % Log4jVersion,
      "org.apache.logging.log4j"              % "log4j-core"                % Log4jVersion,
      "org.apache.logging.log4j"              % "log4j-1.2-api"             % Log4jVersion
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
      "commons-io"                    % "commons-io"              % CommonsIoVersion      % "test",
      "org.mockito"                   % "mockito-all"             % MockitoVersion        % "test",
      "com.fasterxml.jackson.core"    % "jackson-core"            % JacksonVersion        % "test",
      "com.fasterxml.jackson.core"    % "jackson-databind"        % JacksonVersion        % "test",
      "com.fasterxml.jackson.module"  %% "jackson-module-scala"   % JacksonVersion        % "test" exclude("org.scala-lang", "scala-library")
    )
  )
  .dependsOn(core, testkit % "test")

lazy val streams = Project("elastic4s-streams", file("elastic4s-streams"))
  .settings(
    name := "elastic4s-streams",
    libraryDependencies += "com.typesafe.akka"        %% "akka-actor"           % AkkaVersion,
    libraryDependencies += "org.reactivestreams"      % "reactive-streams"      % ReactiveStreamsVersion,
    libraryDependencies += "org.reactivestreams"      % "reactive-streams-tck"  % ReactiveStreamsVersion % "test"
  ).dependsOn(core, testkit % "test", jackson % "test")

lazy val jackson = Project("elastic4s-jackson", file("elastic4s-jackson"))
  .settings(
    name := "elastic4s-jackson",
    libraryDependencies += "com.fasterxml.jackson.core"       % "jackson-core" % JacksonVersion,
    libraryDependencies += "com.fasterxml.jackson.core"       % "jackson-databind" % JacksonVersion,
    libraryDependencies += "com.fasterxml.jackson.module"     %% "jackson-module-scala" % JacksonVersion exclude("org.scala-lang", "scala-library"),
    libraryDependencies += "com.fasterxml.jackson.datatype"   % "jackson-datatype-joda" % JacksonVersion
  ).dependsOn(core, testkit % "test")

lazy val circe = Project("elastic4s-circe", file("elastic4s-circe"))
.settings(
  name := "elastic4s-circe",
  libraryDependencies += "io.circe"       %% "circe-core"     % CirceVersion,
  libraryDependencies +=  "io.circe"      %% "circe-generic" % CirceVersion,
    libraryDependencies +=  "io.circe"    %% "circe-parser"  % CirceVersion
).dependsOn(core, testkit % "test")

lazy val json4s = Project("elastic4s-json4s", file("elastic4s-json4s"))
  .settings(
    name := "elastic4s-json4s",
    libraryDependencies += "org.json4s" %% "json4s-core"    % Json4sVersion,
    libraryDependencies += "org.json4s" %% "json4s-jackson" % Json4sVersion
  ).dependsOn(core, testkit % "test")

lazy val playjson = Project("elastic4s-play-json", file("elastic4s-play-json"))
    .settings(
      name := "elastic4s-play-json",
      libraryDependencies += "com.typesafe.play" %% "play-json" % PlayJsonVersion
    ).dependsOn(core, testkit % "test")
