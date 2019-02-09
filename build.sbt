lazy val root = Project("elastic4s", file("."))
  .settings(
    publish := {},
    publishArtifact := false,
    name := "elastic4s"
  )
  .aggregate(
    core,
    clientesjava,
    cats_effect,
    scalaz,
    monix,
    tests,
    testkit,
    circe,
    jackson,
    json4s,
    playjson,
    sprayjson,
    aws,
    clientsttp,
    clientakka,
    httpstreams,
    akkastreams
  )

lazy val core = Project("elastic4s-core", file("elastic4s-core"))
  .settings(
    name := "elastic4s-core",
    libraryDependencies ++= Seq(
      "joda-time"                    % "joda-time"             % "2.10.1",
      "com.fasterxml.jackson.core"   % "jackson-core"          % JacksonVersion,
      "com.fasterxml.jackson.core"   % "jackson-databind"      % JacksonVersion,
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonVersion
    )
  )

lazy val clientesjava = Project("elastic4s-client-esjava", file("elastic4s-client-esjava"))
  .settings(
    name := "elastic4s-client-esjava",
    libraryDependencies ++= Seq(
      "org.elasticsearch.client"     % "elasticsearch-rest-client" % ElasticsearchVersion,
      "org.apache.logging.log4j"     % "log4j-api"                 % Log4jVersion % "test",
      "com.fasterxml.jackson.core"   % "jackson-core"              % JacksonVersion,
      "com.fasterxml.jackson.core"   % "jackson-databind"          % JacksonVersion,
      "com.fasterxml.jackson.module" %% "jackson-module-scala"     % JacksonVersion exclude ("org.scala-lang", "scala-library")
    )
  )
  .dependsOn(core)

lazy val cats_effect = Project("elastic4s-effect-cats", file("elastic4s-effect-cats"))
  .settings(name := "elastic4s-effect-cats")
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % CatsEffectVersion
    )
  )
  .dependsOn(core)

lazy val scalaz = Project("elastic4s-effect-scalaz", file("elastic4s-effect-scalaz"))
  .settings(name := "elastic4s-effect-scalaz")
  .settings(
    libraryDependencies ++= Seq(
      "org.scalaz" %% "scalaz-core"       % ScalazVersion,
      "org.scalaz" %% "scalaz-concurrent" % ScalazVersion
    )
  )
  .dependsOn(core)

lazy val monix = Project("elastic4s-effect-monix", file("elastic4s-effect-monix"))
  .settings(name := "elastic4s-effect-monix")
  .settings(
    libraryDependencies ++= Seq(
      "io.monix" %% "monix" % MonixVersion
    )
  )
  .dependsOn(core)

lazy val testkit = Project("elastic4s-testkit", file("elastic4s-testkit"))
  .settings(
    name := "elastic4s-testkit",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % ScalatestVersion
    )
  )
  .dependsOn(core, clientesjava)

lazy val httpstreams = Project("elastic4s-http-streams", file("elastic4s-http-streams"))
  .settings(
    name := "elastic4s-http-streams",
    libraryDependencies += "com.typesafe.akka"   %% "akka-actor"          % AkkaVersion,
    libraryDependencies += "org.reactivestreams" % "reactive-streams"     % ReactiveStreamsVersion,
    libraryDependencies += "org.reactivestreams" % "reactive-streams-tck" % ReactiveStreamsVersion % "test"
  )
  .dependsOn(core, testkit % "test", jackson % "test")

lazy val akkastreams = Project("elastic4s-streams-akka", file("elastic4s-streams-akka"))
  .settings(
    name := "elastic4s-streams-akka",
    libraryDependencies += "com.typesafe.akka" % "akka-stream_2.11" % AkkaVersion
  )
  .dependsOn(core, testkit % "test", jackson % "test")

lazy val jackson = Project("elastic4s-json-jackson", file("elastic4s-json-jackson"))
  .settings(
    name := "elastic4s-json-jackson",
    libraryDependencies += "com.fasterxml.jackson.core"     % "jackson-core"          % JacksonVersion,
    libraryDependencies += "com.fasterxml.jackson.core"     % "jackson-databind"      % JacksonVersion,
    libraryDependencies += "com.fasterxml.jackson.module"   %% "jackson-module-scala" % JacksonVersion exclude ("org.scala-lang", "scala-library"),
    libraryDependencies += "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % JacksonVersion
  )
  .dependsOn(core)

lazy val circe = Project("elastic4s-json-circe", file("elastic4s-json-circe"))
  .settings(
    name := "elastic4s-json-circe",
    libraryDependencies += "io.circe" %% "circe-core"    % CirceVersion,
    libraryDependencies += "io.circe" %% "circe-generic" % CirceVersion,
    libraryDependencies += "io.circe" %% "circe-parser"  % CirceVersion
  )
  .dependsOn(core)

lazy val json4s = Project("elastic4s-json-json4s", file("elastic4s-json-json4s"))
  .settings(
    name := "elastic4s-json-json4s",
    libraryDependencies += "org.json4s" %% "json4s-core"    % Json4sVersion,
    libraryDependencies += "org.json4s" %% "json4s-jackson" % Json4sVersion
  )
  .dependsOn(core)

lazy val playjson = Project("elastic4s-json-play", file("elastic4s-json-play"))
  .settings(
    name := "elastic4s-json-play",
    libraryDependencies += "com.typesafe.play" %% "play-json" % PlayJsonVersion
  )
  .dependsOn(core)

lazy val sprayjson = Project("elastic4s-json-spray", file("elastic4s-json-spray"))
  .settings(
    name := "elastic4s-json-spray",
    libraryDependencies += "io.spray" %% "spray-json" % SprayJsonVersion
  )
  .dependsOn(core)

lazy val clientsttp = Project("elastic4s-client-sttp", file("elastic4s-client-sttp"))
  .settings(
    name := "elastic4s-client-sttp",
    libraryDependencies += "com.softwaremill.sttp" %% "core"                             % SttpVersion,
    libraryDependencies += "com.softwaremill.sttp" %% "async-http-client-backend-future" % SttpVersion
  )
  .dependsOn(core)

lazy val clientakka = Project("elastic4s-client-akka", file("elastic4s-client-akka"))
  .settings(
    name := "elastic4s-client-akka",
    libraryDependencies += "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    libraryDependencies += "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    libraryDependencies += "org.scalamock" %% "scalamock" % ScalamockVersion % "test"
  )
  .dependsOn(core, testkit % "test")

lazy val aws = Project("elastic4s-client-aws", file("elastic4s-client-aws"))
  .settings(
    name := "elastic4s-client-aws",
    libraryDependencies += "software.amazon.awssdk" % "auth" % AWSJavaSdkVersion,
    libraryDependencies += "software.amazon.awssdk" % "core" % AWSJavaSdkVersion,
    libraryDependencies += "software.amazon.awssdk" % "regions" % AWSJavaSdkVersion
  )
  .dependsOn(core, clientesjava)

lazy val tests = Project("elastic4s-tests", file("elastic4s-tests"))
  .settings(
    name := "elastic4s-tests",
    libraryDependencies ++= Seq(
      "commons-io"                   % "commons-io"            % CommonsIoVersion % "test",
      "org.mockito"                  % "mockito-all"           % MockitoVersion   % "test",
      "com.fasterxml.jackson.core"   % "jackson-core"          % JacksonVersion   % "test",
      "com.fasterxml.jackson.core"   % "jackson-databind"      % JacksonVersion   % "test",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonVersion   % "test" exclude ("org.scala-lang", "scala-library"),
      "org.apache.logging.log4j"     % "log4j-api"             % "2.11.2"          % "test",
      "org.apache.logging.log4j"     % "log4j-slf4j-impl"      % "2.11.2"          % "test",
      "org.apache.logging.log4j"     % "log4j-core"            % "2.11.2"          % "test"
    ),
    fork in Test := false,
    parallelExecution in Test := false,
    testForkedParallel in Test := false
  )
  .dependsOn(clientesjava, jackson, circe, aws, testkit % "test")

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)
