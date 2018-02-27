import microsites.ExtraMdFileConfig

lazy val root = Project("elastic4s", file("."))
  .settings(
    publish := {},
    publishLocal := {},
    publishArtifact := false,
    name := "elastic4s",
    mappings in (Compile, packageSrc) ++= {
      val base  = (Compile / sourceManaged).value
      val files = (Compile / managedSources).value
      files.map { f =>
        (f, f.relativeTo(base).get.getPath)
      }
    }
  )
  .aggregate(
    core,
    http,
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
    sttp,
    httpstreams,
    embedded
  )

lazy val core = Project("elastic4s-core", file("elastic4s-core"))
  .settings(
    name := "elastic4s-core",
    libraryDependencies ++= Seq(
      "joda-time"                    % "joda-time"             % "2.9.9",
      "com.fasterxml.jackson.core"   % "jackson-core"          % JacksonVersion,
      "com.fasterxml.jackson.core"   % "jackson-databind"      % JacksonVersion,
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonVersion
    )
  )

lazy val http = Project("elastic4s-http", file("elastic4s-http"))
  .settings(
    name := "elastic4s-http",
    libraryDependencies ++= Seq(
      "org.elasticsearch.client"     % "elasticsearch-rest-client" % ElasticsearchVersion,
      "org.apache.logging.log4j"     % "log4j-api"                 % Log4jVersion % "test",
      "com.fasterxml.jackson.core"   % "jackson-core"              % JacksonVersion,
      "com.fasterxml.jackson.core"   % "jackson-databind"          % JacksonVersion,
      "com.fasterxml.jackson.module" %% "jackson-module-scala"     % JacksonVersion exclude ("org.scala-lang", "scala-library")
    )
  )
  .dependsOn(core)

lazy val embedded = Project("elastic4s-embedded", file("elastic4s-embedded"))
  .settings(
    name := "elastic4s-embedded",
    libraryDependencies ++= Seq(
      "org.elasticsearch"                % "elasticsearch"            % ElasticsearchVersion,
      "org.elasticsearch.client"         % "transport"                % ElasticsearchVersion,
      "com.carrotsearch"                 % "hppc"                     % "0.7.1",
      "joda-time"                        % "joda-time"                % "2.9.9",
      "com.fasterxml.jackson.dataformat" % "jackson-dataformat-smile" % JacksonVersion,
      "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor"  % JacksonVersion
//"org.locationtech.spatial4j" % "spatial4j"               % "0.6",
//"com.vividsolutions"         % "jts"                     % "1.13",
//"io.netty"                   % "netty-all"               % "4.1.10.Final",
//"org.apache.lucene"          % "lucene-core"             % LuceneVersion,
//"org.apache.lucene"          % "lucene-analyzers-common" % LuceneVersion,
//"org.apache.lucene"          % "lucene-backward-codecs"  % LuceneVersion,
//"org.apache.lucene"          % "lucene-grouping"         % LuceneVersion,
//"org.apache.lucene"          % "lucene-highlighter"      % LuceneVersion,
//"org.apache.lucene"          % "lucene-join"             % LuceneVersion,
//"org.apache.lucene"          % "lucene-memory"           % LuceneVersion,
//"org.apache.lucene"          % "lucene-misc"             % LuceneVersion,
//"org.apache.lucene"          % "lucene-queries"          % LuceneVersion,
//"org.apache.lucene"          % "lucene-queryparser"      % LuceneVersion,
//"org.apache.lucene"          % "lucene-sandbox"          % LuceneVersion,
//"org.apache.lucene"          % "lucene-spatial"          % LuceneVersion,
//"org.apache.lucene"          % "lucene-spatial-extras"   % LuceneVersion,
//"org.apache.lucene"          % "lucene-spatial3d"        % LuceneVersion,
//"org.apache.lucene"          % "lucene-suggest"          % LuceneVersion,
//"org.apache.lucene"          % "lucene-join"             % LuceneVersion,
//"org.apache.logging.log4j"   % "log4j-api"               % Log4jVersion,
//"org.apache.logging.log4j"   % "log4j-core"              % Log4jVersion,
//"org.apache.logging.log4j"   % "log4j-1.2-api"           % Log4jVersion,
//"org.apache.logging.log4j"   % "log4j-slf4j-impl"        % Log4jVersion,
//"com.fasterxml.jackson.core" % "jackson-core"            % JacksonVersion,
//"com.tdunning"               % "t-digest"                % "3.1"
    )
  )
  .dependsOn(http)

lazy val cats_effect = Project("elastic4s-cats-effect", file("elastic4s-cats-effect"))
  .settings(name := "elastic4s-cats-effect")
  .settings(libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-effect" % "0.8"
  ))
  .dependsOn(http)

lazy val scalaz = Project("elastic4s-scalaz", file("elastic4s-scalaz"))
  .settings(name := "elastic4s-scalaz")
  .settings(libraryDependencies ++= Seq(
    "org.scalaz" %% "scalaz-core" % "7.2.18",
    "org.scalaz" %% "scalaz-concurrent" % "7.2.18"
  ))
  .dependsOn(http)

lazy val monix = Project("elastic4s-monix", file("elastic4s-monix"))
  .settings(name := "elastic4s-monix")
  .settings(libraryDependencies ++= Seq(
    "io.monix" %% "monix" % "2.3.2"
  ))
  .dependsOn(http)

lazy val testkit = Project("elastic4s-testkit", file("elastic4s-testkit"))
  .settings(
    name := "elastic4s-testkit",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % ScalatestVersion
    )
  )
  .dependsOn(core, http)

lazy val httpstreams = Project("elastic4s-http-streams", file("elastic4s-http-streams"))
  .settings(
    name := "elastic4s-http-streams",
    libraryDependencies += "com.typesafe.akka"   %% "akka-actor"          % AkkaVersion,
    libraryDependencies += "org.reactivestreams" % "reactive-streams"     % ReactiveStreamsVersion,
    libraryDependencies += "org.reactivestreams" % "reactive-streams-tck" % ReactiveStreamsVersion % "test"
  )
  .dependsOn(http, testkit % "test", jackson % "test")

lazy val jackson = Project("elastic4s-jackson", file("elastic4s-jackson"))
  .settings(
    name := "elastic4s-jackson",
    libraryDependencies += "com.fasterxml.jackson.core"     % "jackson-core"          % JacksonVersion,
    libraryDependencies += "com.fasterxml.jackson.core"     % "jackson-databind"      % JacksonVersion,
    libraryDependencies += "com.fasterxml.jackson.module"   %% "jackson-module-scala" % JacksonVersion exclude ("org.scala-lang", "scala-library"),
    libraryDependencies += "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % JacksonVersion
  )
  .dependsOn(core)

lazy val circe = Project("elastic4s-circe", file("elastic4s-circe"))
  .settings(
    name := "elastic4s-circe",
    libraryDependencies += "io.circe" %% "circe-core"    % CirceVersion,
    libraryDependencies += "io.circe" %% "circe-generic" % CirceVersion,
    libraryDependencies += "io.circe" %% "circe-parser"  % CirceVersion
  )
  .dependsOn(core)

lazy val json4s = Project("elastic4s-json4s", file("elastic4s-json4s"))
  .settings(
    name := "elastic4s-json4s",
    libraryDependencies += "org.json4s" %% "json4s-core"    % Json4sVersion,
    libraryDependencies += "org.json4s" %% "json4s-jackson" % Json4sVersion
  )
  .dependsOn(core)

lazy val playjson = Project("elastic4s-play-json", file("elastic4s-play-json"))
  .settings(
    name := "elastic4s-play-json",
    libraryDependencies += "com.typesafe.play" %% "play-json" % PlayJsonVersion
  )
  .dependsOn(core)

lazy val sprayjson = Project("elastic4s-spray-json", file("elastic4s-spray-json"))
  .settings(
    name := "elastic4s-spray-json",
    libraryDependencies += "io.spray" %% "spray-json" % SprayJsonVersion
  )
  .dependsOn(core)

lazy val sttp = Project("elastic4s-sttp", file("elastic4s-sttp"))
  .settings(
    name := "elastic4s-sttp",
    libraryDependencies += "com.softwaremill.sttp" %% "core"                             % "1.1.3",
    libraryDependencies += "com.softwaremill.sttp" %% "async-http-client-backend-future" % "1.1.3"
  )
  .dependsOn(core, http)

lazy val aws = Project("elastic4s-aws", file("elastic4s-aws"))
  .settings(
    name := "elastic4s-aws",
    libraryDependencies += "com.amazonaws" % "aws-java-sdk-core" % AWSJavaSdkVersion
  )
  .dependsOn(core, http)

lazy val tests = Project("elastic4s-tests", file("elastic4s-tests"))
  .settings(
    name := "elastic4s-tests",
    libraryDependencies ++= Seq(
      "commons-io"                   % "commons-io"            % CommonsIoVersion % "test",
      "org.mockito"                  % "mockito-all"           % MockitoVersion   % "test",
      "com.fasterxml.jackson.core"   % "jackson-core"          % JacksonVersion   % "test",
      "com.fasterxml.jackson.core"   % "jackson-databind"      % JacksonVersion   % "test",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonVersion   % "test" exclude ("org.scala-lang", "scala-library"),
      "org.apache.logging.log4j"     % "log4j-api"             % "2.8.2"          % "test",
      "org.apache.logging.log4j"     % "log4j-slf4j-impl"      % "2.8.2"          % "test",
      "org.apache.logging.log4j"     % "log4j-core"            % "2.8.2"          % "test"
    ),
    Test / fork := true,
    Test / parallelExecution := false,
    Test / testForkedParallel := false
  )
  .dependsOn(http, jackson, circe, aws, testkit % "test")

lazy val docsMappingsAPIDir = settingKey[String]("Name of subdirectory in site target directory for api docs")

lazy val docs = project
  .in(file("docs"))
  .enablePlugins(MicrositesPlugin, ScalaUnidocPlugin)
  .settings(noPublishSettings)
  .settings(
    micrositeName := "Elastic4s",
    micrositeDescription := "Elasticsearch Scala Client",
    micrositeAuthor := "Stephen Samuel",
    micrositeHomepage := "https://sksamuel.github.io/elastic4s",
    micrositeGithubOwner := "sksamuel",
    micrositeGithubRepo := "elastic4s",
    micrositeBaseUrl := "/elastic4s",
    micrositeDocumentationUrl := "docs",
    micrositeTwitter := "",
    micrositeHighlightTheme := "atom-one-light",
    micrositeExtraMdFiles := Map(file("README.md") -> ExtraMdFileConfig("index.md", "home")),
    micrositePalette := Map(
      "brand-primary"   -> "#729B79",
      "brand-secondary" -> "#2E2C2F",
      "brand-tertiary"  -> "#2B2D42",
      "gray-dark"       -> "#646767",
      "gray"            -> "#475B63",
      "gray-light"      -> "#8D99AE",
      "gray-lighter"    -> "#EAF2E6",
      "white-color"     -> "#FFFFFF"
    ),
    git.remoteRepo := "git@github.com:sksamuel/elastic4s.git",
    autoAPIMappings := true,
    docsMappingsAPIDir := "api",
    addMappingsToSiteDir(ScalaUnidoc / packageDoc / mappings, docsMappingsAPIDir),
    ghpagesNoJekyll := false,
    tut / fork := false,
    ScalaUnidoc / unidoc / fork := false,
    makeSite / includeFilter := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf" | "*.yml" | "*.md",
    // push microsite on release
    releaseProcess += releaseStepTask(publishMicrosite)
  )
  .dependsOn(core, embedded, http, circe, aws)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)
