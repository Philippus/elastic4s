import com.typesafe.sbt.pgp.PgpKeys
import sbt._
import sbt.plugins.JvmPlugin
import sbt.Keys._

object Build extends AutoPlugin {

  override def trigger = AllRequirements
  override def requires = JvmPlugin

  object autoImport {
    val org = "com.sksamuel.elastic4s"
    val ScalaVersion = "2.11.8"
    val ScalatestVersion = "3.0.0"
    val ScalacticVersion = "3.0.0"
    val MockitoVersion = "1.9.5"
    val AkkaVersion = "2.3.12"
    val ReactiveStreamsVersion = "1.0.0"
    val JacksonVersion = "2.7.5"
    val Slf4jVersion = "1.7.12"
    val ElasticsearchVersion = "5.0.0"
    val Log4jVersion = "1.2.17"
    val CommonsIoVersion = "2.4"
    val CirceVersion = "0.4.1"
    val PlayJsonVersion = "2.5.9"
  }

  import autoImport._

  override def projectSettings = Seq(
    organization := org,
    scalaVersion := ScalaVersion,
    crossScalaVersions := Seq("2.11.8"),
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
      "org.elasticsearch.client"      % "transport"                 % ElasticsearchVersion,
      "org.apache.lucene"             % "lucene-join"               % "6.2.1",
      "org.scalactic"                 %% "scalactic"                % ScalacticVersion,
      "org.slf4j"                     % "slf4j-api"                 % Slf4jVersion,
      "commons-io"                    % "commons-io"                % CommonsIoVersion  % "test",
      "log4j"                         % "log4j"                     % Log4jVersion      % "test",
      "org.slf4j"                     % "log4j-over-slf4j"          % Slf4jVersion      % "test",
      "org.mockito"                   % "mockito-all"               % MockitoVersion    % "test",
      "org.scalatest"                 %% "scalatest"                % ScalatestVersion  % "test"
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
