import com.typesafe.sbt.SbtGhPages.GhPagesKeys._
import microsites.ExtraMdFileConfig

lazy val root = Project("elastic4s", file("."))
  .settings(publish := {})
  .settings(publishArtifact := false)
  .settings(name := projectName)
  .settings(mappings in(Compile, packageSrc) ++= {
    val base = (sourceManaged in Compile).value
    val files = (managedSources in Compile).value
    files.map { f => (f, f.relativeTo(base).get.getPath) }
  })
  .aggregate(
    core,
    tcp,
    http,
  //  tests,
    embedded,
    testkit,
    circe,
    jackson,
    json4s,
    playjson,
    sprayjson,
    streams,
    httpstreams,
    xpacksecurity
  )

lazy val core = Project("elastic4s-core", file("elastic4s-core"))
  .settings(name := s"$projectName-core")
  .settings(libraryDependencies ++= Seq(
    "org.locationtech.spatial4j"    % "spatial4j"     % "0.6",
    "com.vividsolutions"            % "jts"           % "1.13",
    "com.fasterxml.jackson.core"    % "jackson-core"            % JacksonVersion        % "test",
    "com.fasterxml.jackson.core"    % "jackson-databind"        % JacksonVersion        % "test",
    "com.fasterxml.jackson.module"  %% "jackson-module-scala"   % JacksonVersion        % "test" exclude("org.scala-lang", "scala-library")
  ))

lazy val tcp = Project("elastic4s-tcp", file("elastic4s-tcp"))
  .settings(name := s"$projectName-tcp")
    .settings(libraryDependencies ++= Seq(
      "io.netty"                              % "netty-all"                 % "4.1.15.Final",
      "org.apache.lucene"                     % "lucene-core"               % LuceneVersion,
      "org.apache.lucene"                     % "lucene-analyzers-common"   % LuceneVersion,
      "org.apache.lucene"                     % "lucene-backward-codecs"    % LuceneVersion,
      "org.apache.lucene"                     % "lucene-grouping"           % LuceneVersion,
      "org.apache.lucene"                     % "lucene-highlighter"        % LuceneVersion,
      "org.apache.lucene"                     % "lucene-join"               % LuceneVersion,
      "org.apache.lucene"                     % "lucene-memory"             % LuceneVersion,
      "org.apache.lucene"                     % "lucene-misc"               % LuceneVersion,
      "org.apache.lucene"                     % "lucene-queries"            % LuceneVersion,
      "org.apache.lucene"                     % "lucene-queryparser"        % LuceneVersion,
      "org.apache.lucene"                     % "lucene-sandbox"            % LuceneVersion,
      "org.apache.lucene"                     % "lucene-spatial"            % LuceneVersion,
      "org.apache.lucene"                     % "lucene-spatial-extras"     % LuceneVersion,
      "org.apache.lucene"                     % "lucene-spatial3d"          % LuceneVersion,
      "org.apache.lucene"                     % "lucene-suggest"            % LuceneVersion,
      "org.elasticsearch.client"              % "transport"                 % ElasticsearchVersion,
      "org.apache.lucene"                     % "lucene-join"               % LuceneVersion,
      "org.apache.logging.log4j"              % "log4j-api"                 % Log4jVersion,
      "org.apache.logging.log4j"              % "log4j-core"                % Log4jVersion,
      "org.apache.logging.log4j"              % "log4j-1.2-api"             % Log4jVersion,
      "com.carrotsearch"                      % "hppc"                      % "0.7.2",
      "joda-time"                             % "joda-time"                 % "2.9.9",
      "com.fasterxml.jackson.core"            % "jackson-core"              % JacksonVersion,
      "com.tdunning"                          % "t-digest"                  % "3.2"
    ))
  .dependsOn(core)

lazy val http = Project("elastic4s-http", file("elastic4s-http"))
  .settings(
    name := s"$projectName-http",
    libraryDependencies ++= Seq(
      "org.elasticsearch.client"      % "rest"                    % ElasticsearchVersion,
      "org.apache.httpcomponents"     % "httpclient"              % "4.5.3",
      "org.apache.httpcomponents"     % "httpcore-nio"            % "4.4.6",
      "org.apache.httpcomponents"     % "httpcore"                % "4.4.6",
      "org.apache.httpcomponents"     % "httpasyncclient"         % "4.1.3",
      "com.amazonaws"                 %  "aws-java-sdk-core"      % "1.11.192",
      "io.ticofab"                    %% "aws-request-signer"     % "0.5.1"
        exclude("com.amazonaws", "aws-java-sdk-core"),
      "org.apache.logging.log4j"      % "log4j-api"               % Log4jVersion  % "test",
      "com.fasterxml.jackson.core"    % "jackson-core"            % JacksonVersion,
      "com.fasterxml.jackson.core"    % "jackson-databind"        % JacksonVersion,
      "com.fasterxml.jackson.module" %% "jackson-module-scala"    % JacksonVersion exclude("org.scala-lang", "scala-library")
    )
  )
  .dependsOn(core)

lazy val xpacksecurity = Project("elastic4s-xpack-security", file("elastic4s-xpack-security"))
  .settings(
    name := s"$projectName-xpack-security",
    resolvers += "elastic" at "https://artifacts.elastic.co/maven",
    libraryDependencies += "org.elasticsearch.client" % "x-pack-transport" % ElasticsearchVersion
  ).dependsOn(tcp, testkit % "test")

lazy val embedded = Project("elastic4s-embedded", file("elastic4s-embedded"))
  .settings(
    name := s"$projectName-embedded",
    libraryDependencies ++= Seq(
      "org.elasticsearch"                     % "elasticsearch"             % ElasticsearchVersion,
      "com.fasterxml.jackson.dataformat"      % "jackson-dataformat-smile"  % JacksonVersion,
      "com.fasterxml.jackson.dataformat"      % "jackson-dataformat-cbor"   % JacksonVersion
    )
  )
  .dependsOn(tcp)

lazy val testkit = Project("elastic4s-testkit", file("elastic4s-testkit"))
  .settings(
    name := s"$projectName-testkit",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % ScalatestVersion
    )
  )
  .dependsOn(core, embedded, http)

//lazy val tests = Project("elastic4s-tests", file("elastic4s-tests"))
//  .settings(
//    name := s"$projectName-tests",
//    libraryDependencies ++= Seq(
//      "commons-io"                    % "commons-io"              % CommonsIoVersion      % "test",
//      "org.mockito"                   % "mockito-all"             % MockitoVersion        % "test",
//      "com.fasterxml.jackson.core"    % "jackson-core"            % JacksonVersion        % "test",
//      "com.fasterxml.jackson.core"    % "jackson-databind"        % JacksonVersion        % "test",
//      "com.fasterxml.jackson.module"  %% "jackson-module-scala"   % JacksonVersion        % "test" exclude("org.scala-lang", "scala-library"),
//      "org.apache.logging.log4j"      % "log4j-api"               % "2.7"                 % "test",
//      "org.apache.logging.log4j"      % "log4j-slf4j-impl"        % "2.7"                 % "test"
//    )
//  )
//  .dependsOn(tcp, http, jackson, testkit % "test")

lazy val streams = Project("elastic4s-streams", file("elastic4s-streams"))
  .settings(
    name := s"$projectName-streams",
    libraryDependencies += "com.typesafe.akka"        %% "akka-actor"           % AkkaVersion,
    libraryDependencies += "org.reactivestreams"      % "reactive-streams"      % ReactiveStreamsVersion,
    libraryDependencies += "org.reactivestreams"      % "reactive-streams-tck"  % ReactiveStreamsVersion % "test"
  ).dependsOn(tcp, testkit % "test", jackson % "test")

lazy val httpstreams = Project("elastic4s-http-streams", file("elastic4s-http-streams"))
  .settings(
    name := s"$projectName-http-streams",
    libraryDependencies += "com.typesafe.akka"        %% "akka-actor"           % AkkaVersion,
    libraryDependencies += "org.reactivestreams"      % "reactive-streams"      % ReactiveStreamsVersion,
    libraryDependencies += "org.reactivestreams"      % "reactive-streams-tck"  % ReactiveStreamsVersion % "test"
  ).dependsOn(http, testkit % "test", jackson % "test")

lazy val jackson = Project("elastic4s-jackson", file("elastic4s-jackson"))
  .settings(
    name := s"$projectName-jackson",
    libraryDependencies += "com.fasterxml.jackson.core"       % "jackson-core" % JacksonVersion,
    libraryDependencies += "com.fasterxml.jackson.core"       % "jackson-databind" % JacksonVersion,
    libraryDependencies += "com.fasterxml.jackson.module"     %% "jackson-module-scala" % JacksonVersion exclude("org.scala-lang", "scala-library"),
    libraryDependencies += "com.fasterxml.jackson.datatype"   % "jackson-datatype-joda" % JacksonVersion
  ).dependsOn(core, testkit % "test")

lazy val circe = Project("elastic4s-circe", file("elastic4s-circe"))
  .settings(
    name := s"$projectName-circe",
    libraryDependencies += "io.circe" %% "circe-core"     % CirceVersion,
    libraryDependencies += "io.circe" %% "circe-generic"  % CirceVersion,
    libraryDependencies += "io.circe" %% "circe-parser"   % CirceVersion
  ).dependsOn(core, testkit % "test")

lazy val json4s = Project("elastic4s-json4s", file("elastic4s-json4s"))
  .settings(
    name := s"$projectName-json4s",
    libraryDependencies += "org.json4s" %% "json4s-core"    % Json4sVersion,
    libraryDependencies += "org.json4s" %% "json4s-jackson" % Json4sVersion
  ).dependsOn(core, testkit % "test")

lazy val playjson = Project("elastic4s-play-json", file("elastic4s-play-json"))
  .settings(
    name := s"$projectName-play-json",
    libraryDependencies += "com.typesafe.play" %% "play-json" % PlayJsonVersion
  ).dependsOn(core, testkit % "test")

lazy val sprayjson = Project("elastic4s-spray-json", file("elastic4s-spray-json"))
  .settings(
    name := s"$projectName-spray-json",
    libraryDependencies += "io.spray" %% "spray-json" % SprayJsonVersion
  ).dependsOn(core, testkit % "test")

lazy val docsMappingsAPIDir = settingKey[String]("Name of subdirectory in site target directory for api docs")

lazy val docs = project
  .in(file("docs"))
  .enablePlugins(MicrositesPlugin, ScalaUnidocPlugin)
  .settings(ghpages.settings)
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
      "brand-primary" -> "#729B79",
      "brand-secondary" -> "#2E2C2F",
      "brand-tertiary" -> "#2B2D42",
      "gray-dark" -> "#646767",
      "gray" -> "#475B63",
      "gray-light" -> "#8D99AE",
      "gray-lighter" -> "#EAF2E6",
      "white-color" -> "#FFFFFF"
    ),
    git.remoteRepo := "git@github.com:sksamuel/elastic4s.git",
    autoAPIMappings := true,
    docsMappingsAPIDir := "api",
    addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), docsMappingsAPIDir),
    ghpagesNoJekyll := false,
    fork in tut := true,
    fork in (ScalaUnidoc, unidoc) := true,
    includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf" | "*.yml" | "*.md",
    // push microsite on release
    releaseProcess += releaseStepTask(publishMicrosite)
  ).dependsOn(core, embedded, http, circe)

lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)
