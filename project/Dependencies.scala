import sbt.Keys.libraryDependencies
import sbt._

object Dependencies {
  val AkkaHttpVersion                = "10.2.10"
  val AkkaVersion                    = "2.6.20"
  val CatsEffect2Version             = "2.5.5"
  val CatsEffectVersion              = "3.2.8"
  val CatsVersion                    = "2.0.0"
  val CirceVersion                   = "0.14.3"
  val CommonsIoVersion               = "2.11.0"
  val ElasticsearchVersion           = "8.5.3"
  val ExtsVersion                    = "1.61.1"
  val JacksonVersion                 = "2.14.1"
  val Json4sVersion                  = "4.0.6"
  val Log4jVersion                   = "2.15.0"
  val MockitoVersion                 = "4.10.0"
  val MonixVersion                   = "3.4.1"
  val PlayJsonVersion                = "2.10.0-RC6"
  val ReactiveStreamsVersion         = "1.0.3"
  val ScalamockVersion               = "5.2.0"
  val ScalatestPlusMockitoArtifactId = "mockito-3-4"
  val ScalatestPlusVersion           = "3.2.9.0"
  val ScalazVersion                  = "7.2.35"
  val ScalatestVersion               = "3.2.13"
  val Slf4jVersion                   = "2.0.6"
  val SprayJsonVersion               = "1.3.6"
  val SttpVersion                    = "3.8.3"
  val ZIOJson1Version                = "0.1.5"
  val ZIO1Version                    = "1.0.17"
  val ZIOJsonVersion                 = "0.3.0"
  val ZIOVersion                     = "2.0.4"

  lazy val commonDeps = Seq(
    libraryDependencies ++= Seq(
      "com.typesafe"       % "config"                       % "1.4.2",
      "org.slf4j"          % "slf4j-api"                    % Slf4jVersion,
      "org.scalatest"     %% "scalatest"                    % ScalatestVersion     % "test",
      "org.mockito"        % "mockito-core"                 % MockitoVersion       % "test",
      "org.scalatestplus" %% ScalatestPlusMockitoArtifactId % ScalatestPlusVersion % "test"
    ))

  lazy val fasterXmlJacksonScala = Seq(
    "com.fasterxml.jackson.core"    % "jackson-core"         % JacksonVersion,
    "com.fasterxml.jackson.core"    % "jackson-databind"     % JacksonVersion,
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonVersion
  )

  lazy val zio1 = Seq("dev.zio" %% "zio" % ZIO1Version)
  lazy val zio = Seq("dev.zio" %% "zio" % ZIOVersion)

  lazy val scalaz =
    Seq("org.scalaz" %% "scalaz-core" % ScalazVersion, "org.scalaz" %% "scalaz-concurrent" % ScalazVersion)

  lazy val circe = Seq(
    "io.circe" %% "circe-core"    % CirceVersion,
    "io.circe" %% "circe-generic" % CirceVersion,
    "io.circe" %% "circe-parser"  % CirceVersion)

  lazy val akkaActor                    = "com.typesafe.akka"             %% "akka-actor"                       % AkkaVersion
  lazy val akkaHTTP                     = "com.typesafe.akka"             %% "akka-http"                        % AkkaHttpVersion
  lazy val akkaStream                   = "com.typesafe.akka"             %% "akka-stream"                      % AkkaVersion
  lazy val asyncHttpClientBackendFuture = "com.softwaremill.sttp.client3" %% "async-http-client-backend-future" % SttpVersion
  lazy val cats                         = "org.typelevel"                 %% "cats-effect"                      % CatsEffectVersion
  lazy val cats2                        = "org.typelevel"                 %% "cats-effect"                      % CatsEffect2Version
  lazy val elasticsearchRestClient      = "org.elasticsearch.client"       % "elasticsearch-rest-client"        % ElasticsearchVersion
  lazy val json4s                       = Seq("org.json4s" %% "json4s-core" % Json4sVersion, "org.json4s" %% "json4s-jackson" % Json4sVersion)
  lazy val monix                        = "io.monix"                      %% "monix"      % MonixVersion
  lazy val playJson                     = Seq("com.typesafe.play"         %% "play-json"  % PlayJsonVersion)
  lazy val sprayJson                    = Seq("io.spray"                  %% "spray-json" % SprayJsonVersion)
  lazy val sttp                         = "com.softwaremill.sttp.client3" %% "core"       % SttpVersion
  lazy val zioJson1                     = "dev.zio"                       %% "zio-json"   % ZIOJson1Version
  lazy val zioJson                      = "dev.zio"                       %% "zio-json"   % ZIOJsonVersion
  lazy val elasticsearchRestClientSniffer = "org.elasticsearch.client" % "elasticsearch-rest-client-sniffer" %
    ElasticsearchVersion

  lazy val commonsIo             = "commons-io"               % "commons-io"                   % CommonsIoVersion       % "test"
  lazy val log4jApi              = "org.apache.logging.log4j" % "log4j-api"                    % Log4jVersion           % "test"
  lazy val mockitoCore           = "org.mockito"              % "mockito-core"                 % MockitoVersion         % "test"
  lazy val reactiveStreamsTck    = "org.reactivestreams"      % "reactive-streams-tck"         % ReactiveStreamsVersion % "test"
  lazy val scalaMock             = "org.scalamock"           %% "scalamock"                    % ScalamockVersion       % "test"
  lazy val scalaTestMain         = "org.scalatest"           %% "scalatest"                    % ScalatestVersion
  lazy val scalaTest             = scalaTestMain % "test"
  lazy val scalaTestPlusMokito   = "org.scalatestplus"       %% ScalatestPlusMockitoArtifactId % ScalatestPlusVersion
  lazy val scalaTestPlusTestng67 = "org.scalatestplus"       %% "testng-6-7"                   % ScalatestPlusVersion   % "test"

}
