import sbt.Keys.libraryDependencies
import sbt._

object Dependencies {
  val AkkaHttpVersion                = "10.2.10"
  val AkkaVersion                    = "2.6.21"
  val CatsEffect2Version             = "2.5.5"
  val CatsEffectVersion              = "3.5.7"
  val CirceVersion                   = "0.14.10"
  val CommonsIoVersion               = "2.18.0"
  val ElasticsearchVersion           = "8.17.2"
  val ExtsVersion                    = "1.61.1"
  val Http4sVersion                  = "0.23.30"
  val JacksonVersion                 = "2.18.2"
  val Json4sVersion                  = "4.0.7"
  val Log4jVersion                   = "2.24.3"
  val MockitoVersion                 = "5.15.2"
  val MonixVersion                   = "3.4.1"
  val PekkoHttpVersion               = "1.1.0"
  val PekkoVersion                   = "1.1.3"
  val PlayJsonVersion                = "3.0.4"
  val ReactiveStreamsVersion         = "1.0.4"
  val ScalatestPlusMockitoArtifactId = "mockito-5-12"
  val ScalatestPlusVersion           = "3.2.19.0"
  val ScalazVersion                  = "7.2.36"
  val ScalatestVersion               = "3.2.19"
  val Slf4jVersion                   = "2.0.17"
  val SprayJsonVersion               = "1.3.6"
  val SttpVersion                    = "3.10.3"
  val ZIOJson1Version                = "0.1.5"
  val ZIO1Version                    = "1.0.18"
  val ZIOVersion                     = "2.1.16"
  val ZIOJsonVersion                 = "0.7.36"

  lazy val commonDeps = Seq(
    libraryDependencies ++= Seq(
      "com.typesafe"       % "config"                       % "1.4.3",
      "org.slf4j"          % "slf4j-api"                    % Slf4jVersion,
      "org.scalatest"     %% "scalatest"                    % ScalatestVersion     % Test,
      "org.mockito"        % "mockito-core"                 % MockitoVersion       % Test,
      "org.scalatestplus" %% ScalatestPlusMockitoArtifactId % ScalatestPlusVersion % Test
    )
  )

  lazy val fasterXmlJacksonScala = Seq(
    "com.fasterxml.jackson.core"    % "jackson-core"         % JacksonVersion,
    "com.fasterxml.jackson.core"    % "jackson-databind"     % JacksonVersion,
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonVersion
  )

  lazy val zio1 = "dev.zio" %% "zio" % ZIO1Version
  lazy val zio  = "dev.zio" %% "zio" % ZIOVersion

  lazy val scalaz =
    Seq("org.scalaz" %% "scalaz-core" % ScalazVersion, "org.scalaz" %% "scalaz-concurrent" % ScalazVersion)

  lazy val circe = Seq(
    "io.circe" %% "circe-core"    % CirceVersion,
    "io.circe" %% "circe-generic" % CirceVersion,
    "io.circe" %% "circe-parser"  % CirceVersion
  )

  lazy val akkaActor                      = "com.typesafe.akka"             %% "akka-actor"                        % AkkaVersion
  lazy val akkaHTTP                       = "com.typesafe.akka"             %% "akka-http"                         % AkkaHttpVersion
  lazy val akkaStream                     = "com.typesafe.akka"             %% "akka-stream"                       % AkkaVersion
  lazy val cats                           = "org.typelevel"                 %% "cats-effect"                       % CatsEffectVersion
  lazy val cats2                          = "org.typelevel"                 %% "cats-effect"                       % CatsEffect2Version
  lazy val elasticsearchRestClient        = "org.elasticsearch.client"       % "elasticsearch-rest-client"         % ElasticsearchVersion
  lazy val http4sClient                   = "org.http4s"                    %% "http4s-client"                     % Http4sVersion
  lazy val http4sEmberClient              = "org.http4s"                    %% "http4s-ember-client"               % Http4sVersion
  lazy val json4s                         = Seq("org.json4s" %% "json4s-core" % Json4sVersion, "org.json4s" %% "json4s-jackson" % Json4sVersion)
  lazy val monix                          = "io.monix"                      %% "monix"                             % MonixVersion
  lazy val pekkoActor                     = "org.apache.pekko"              %% "pekko-actor"                       % PekkoVersion
  lazy val pekkoHTTP                      = "org.apache.pekko"              %% "pekko-http"                        % PekkoHttpVersion
  lazy val pekkoStream                    = "org.apache.pekko"              %% "pekko-stream"                      % PekkoVersion
  lazy val playJson                       = "org.playframework"             %% "play-json"                         % PlayJsonVersion
  lazy val sprayJson                      = "io.spray"                      %% "spray-json"                        % SprayJsonVersion
  lazy val sttp                           = "com.softwaremill.sttp.client3" %% "core"                              % SttpVersion
  lazy val zioJson1                       = "dev.zio"                       %% "zio-json"                          % ZIOJson1Version
  lazy val zioJson                        = "dev.zio"                       %% "zio-json"                          % ZIOJsonVersion
  lazy val elasticsearchRestClientSniffer = "org.elasticsearch.client"       % "elasticsearch-rest-client-sniffer" %
    ElasticsearchVersion

  lazy val commonsIo            = "commons-io"               % "commons-io"                   % CommonsIoVersion       % Test
  lazy val log4jApi             = "org.apache.logging.log4j" % "log4j-api"                    % Log4jVersion           % Test
  lazy val mockitoCore          = "org.mockito"              % "mockito-core"                 % MockitoVersion         % Test
  lazy val reactiveStreamsTck   = "org.reactivestreams"      % "reactive-streams-tck"         % ReactiveStreamsVersion % Test
  lazy val scalaTestMain        = "org.scalatest"           %% "scalatest"                    % ScalatestVersion
  lazy val scalaTest            = scalaTestMain              % Test
  lazy val scalaTestPlusMockito = "org.scalatestplus"       %% ScalatestPlusMockitoArtifactId % ScalatestPlusVersion
  lazy val scalaTestPlusTestng  = "org.scalatestplus"       %% "testng-7-10"                  % ScalatestPlusVersion   % Test

}
