import com.typesafe.sbt.pgp.PgpKeys
import sbt._
import sbt.plugins.JvmPlugin
import sbt.Keys._

object Build extends AutoPlugin {

  override def trigger = AllRequirements
  override def requires = JvmPlugin

  object autoImport {
    val org = "com.sksamuel.elastic4s"
    val AkkaVersion = "2.4.16"
    val CatsVersion = "0.8.1"
    val CirceVersion = "0.6.1"
    val CommonsIoVersion = "2.4"
    val ElasticsearchVersion = "5.1.1"
    val ExtsVersion = "1.37.0"
    val JacksonVersion = "2.8.4"
    val Json4sVersion = "3.5.0"
    val Log4jVersion = "2.6.2"
    val LuceneVersion = "6.3.0"
    val MockitoVersion = "1.9.5"
    val PlayJsonVersion = "2.6.0-M1"
    val ReactiveStreamsVersion = "1.0.0"
    val ScalaVersion = "2.12.1"
    val ScalatestVersion = "3.0.1"
    val Slf4jVersion = "1.7.12"
  }

  import autoImport._

  override def projectSettings = Seq(
    organization := org,
    // a 'compileonly' configuation
    ivyConfigurations += config("compileonly").hide,
    // some compileonly dependency
    libraryDependencies += "commons-io" % "commons-io" % "2.4" % "compileonly",
    // appending everything from 'compileonly' to unmanagedClasspath
    unmanagedClasspath in Compile ++= update.value.select(configurationFilter("compileonly")),
    scalaVersion := ScalaVersion,
    crossScalaVersions := Seq("2.11.8", "2.12.1"),
    publishMavenStyle := true,
    resolvers += Resolver.mavenLocal,
    fork in Test := true,
    javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled"),
    publishArtifact in Test := false,
    parallelExecution in Test := false,
    sbtrelease.ReleasePlugin.autoImport.releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    sbtrelease.ReleasePlugin.autoImport.releaseCrossBuild := true,
    scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
    javacOptions := Seq("-source", "1.7", "-target", "1.7"),
    libraryDependencies ++= Seq(
      "org.elasticsearch.client"              % "transport"                 % ElasticsearchVersion,
      "org.elasticsearch.client"              % "rest"                      % ElasticsearchVersion,
      "org.apache.lucene"                     % "lucene-join"               % LuceneVersion,
      "com.sksamuel.exts"                     %% "exts"                     % ExtsVersion,
      "org.typelevel"                         %% "cats"                     % CatsVersion,
      "org.slf4j"                             % "slf4j-api"                 % Slf4jVersion,
      "com.vividsolutions"                    % "jts"                       % "1.13",
      "io.netty"                              % "netty-all"                 % "4.1.6.Final",
      "org.apache.logging.log4j"              % "log4j-api"                 % "2.7",
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
      "org.locationtech.spatial4j"            % "spatial4j"                 % "0.6",
      "org.apache.httpcomponents"             % "httpclient"                % "4.5.2",
      "org.mockito"                           % "mockito-all"               % MockitoVersion        % "test",
      "org.scalatest"                         %% "scalatest"                % ScalatestVersion      % "test"
    ),
    publishTo <<= version {
      (v: String) =>
        val nexus = "https://oss.sonatype.org/"
        if (v.trim.endsWith("SNAPSHOT"))
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra := {
      <url>https://github.com/sksamuel/elastic4s</url>
        <licenses>
          <license>
            <name>Apache 2</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:sksamuel/elastic4s.git</url>
          <connection>scm:git@github.com:sksamuel/elastic4s.git</connection>
        </scm>
        <developers>
          <developer>
            <id>sksamuel</id>
            <name>sksamuel</name>
            <url>http://github.com/sksamuel</url>
          </developer>
        </developers>
    }
  )
}
