import sbt.Keys.libraryDependencies
import sbt._

object Dependencies {
  val AkkaHttpVersion                = "10.5.0"
  val AkkaVersion                    = "2.8.0"
  val CatsVersion                    = "2.13.0"
  val CatsEffectVersion              = "3.6.1"
  val CirceVersion                   = "0.14.15"
  val CommonsIoVersion               = "2.21.0"
  val ElasticsearchVersion           = "9.3.3"
  val ExtsVersion                    = "1.61.1"
  val Http4sVersion                  = "0.23.34"
  val JacksonVersion                 = "3.1.2"
  val Json4sVersion                  = "4.1.0"
  val Log4jVersion                   = "2.25.4"
  val MockitoVersion                 = "5.23.0"
  val MonixVersion                   = "3.4.1"
  val PekkoHttpVersion               = "1.3.0"
  val PekkoVersion                   = "1.5.0"
  val PlayJsonVersion                = "3.0.6"
  val ReactiveStreamsVersion         = "1.0.4"
  val ScalatestPlusMockitoArtifactId = "mockito-5-23"
  val ScalatestPlusVersion           = "3.2.20.0"
  val ScalazVersion                  = "7.2.36"
  val ScalatestVersion               = "3.2.20"
  val Slf4jVersion                   = "2.0.17"
  val SprayJsonVersion               = "1.3.6"
  val SttpVersion                    = "3.11.0"
  val Sttp4Version                   = "4.0.22"
  val ZIOJson1Version                = "0.1.5"
  val ZIO1Version                    = "1.0.18"
  val ZIOVersion                     = "2.1.25"
  val ZIOJsonVersion                 = "0.9.1"
  val ZIOHttpVersion                 = "3.10.1"

  lazy val commonDeps = Seq(
    libraryDependencies ++= Seq(
      "com.typesafe"   % "config"    % "1.4.6",
      "org.slf4j"      % "slf4j-api" % Slf4jVersion,
      "org.scalatest" %% "scalatest" % ScalatestVersion % Test
    )
  )

  lazy val fasterXmlJacksonCore        = "tools.jackson.core"    % "jackson-core"         % JacksonVersion
  lazy val fasterXmlJacksonDatabind    = "tools.jackson.core"    % "jackson-databind"     % JacksonVersion
  lazy val fasterXmlJacksonModuleScala = "tools.jackson.module" %% "jackson-module-scala" % JacksonVersion

  lazy val fasterXmlJacksonScala = Seq(fasterXmlJacksonCore, fasterXmlJacksonDatabind, fasterXmlJacksonModuleScala)

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
  lazy val cats                           = "org.typelevel"                 %% "cats-core"                         % CatsVersion
  lazy val catsEffect                     = "org.typelevel"                 %% "cats-effect"                       % CatsEffectVersion
  lazy val elasticsearchRestClient        = "org.elasticsearch.client"       % "elasticsearch-rest-client"         % ElasticsearchVersion
  lazy val http4sClient                   = "org.http4s"                    %% "http4s-client"                     % Http4sVersion
  lazy val http4sEmberClient              = "org.http4s"                    %% "http4s-ember-client"               % Http4sVersion
  lazy val json4s                         =
    Seq("io.github.json4s" %% "json4s-core" % Json4sVersion, "io.github.json4s" %% "json4s-jackson" % Json4sVersion)
  lazy val monix                          = "io.monix"                      %% "monix"                             % MonixVersion
  lazy val pekkoActor                     = "org.apache.pekko"              %% "pekko-actor"                       % PekkoVersion
  lazy val pekkoHTTP                      = "org.apache.pekko"              %% "pekko-http"                        % PekkoHttpVersion
  lazy val pekkoStream                    = "org.apache.pekko"              %% "pekko-stream"                      % PekkoVersion
  lazy val playJson                       = "org.playframework"             %% "play-json"                         % PlayJsonVersion
  lazy val sprayJson                      = "io.spray"                      %% "spray-json"                        % SprayJsonVersion
  lazy val sttp                           = "com.softwaremill.sttp.client3" %% "core"                              % SttpVersion
  lazy val sttp4                          = "com.softwaremill.sttp.client4" %% "core"                              % Sttp4Version
  lazy val zioJson1                       = "dev.zio"                       %% "zio-json"                          % ZIOJson1Version
  lazy val zioJson                        = "dev.zio"                       %% "zio-json"                          % ZIOJsonVersion
  lazy val zioHttp                        = "dev.zio"                       %% "zio-http"                          % ZIOHttpVersion
  lazy val elasticsearchRestClientSniffer = "org.elasticsearch.client"       % "elasticsearch-rest-client-sniffer" %
    ElasticsearchVersion

  lazy val commonsIo            = "commons-io"               % "commons-io"                   % CommonsIoVersion       % Test
  lazy val log4jApi             = "org.apache.logging.log4j" % "log4j-api"                    % Log4jVersion           % Test
  lazy val mockitoCore          = "org.mockito"              % "mockito-core"                 % MockitoVersion         % Test
  lazy val reactiveStreamsTck   = "org.reactivestreams"      % "reactive-streams-tck"         % ReactiveStreamsVersion % Test
  lazy val scalaTestMain        = "org.scalatest"           %% "scalatest"                    % ScalatestVersion
  lazy val scalaTest            = scalaTestMain              % Test
  lazy val scalaTestPlusMockito = "org.scalatestplus"       %% ScalatestPlusMockitoArtifactId % ScalatestPlusVersion
  lazy val scalaTestPlusTestng  = "org.scalatestplus"       %% "testng-7-12"                  % ScalatestPlusVersion   % Test

}
