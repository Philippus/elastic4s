import com.typesafe.sbt.SbtPgp
import com.typesafe.sbt.pgp.PgpKeys
import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin

object Build extends AutoPlugin {

  override def trigger  = AllRequirements
  override def requires = JvmPlugin

  object autoImport {
    val org = "com.sksamuel.elastic4s"
    val ScalaVersion = "2.11.12"
    val ScalatestVersion = "3.0.0"
    val MockitoVersion = "1.9.5"
    val JacksonVersion = "2.9.4"
    val Slf4jVersion = "1.7.7"
    val ScalaLoggingVersion = "3.9.0"
    val ElasticsearchVersion = "1.5.2"
  }

  import autoImport._

  override val projectSettings = Seq(
    organization := org,
    scalaVersion := ScalaVersion,
    crossScalaVersions := Seq("2.12.6", "2.11.12"),
    resolvers += Resolver.mavenLocal,
    resolvers += Resolver.url("https://artifacts.elastic.co/maven"),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    parallelExecution in Test := false,
    SbtPgp.autoImport.useGpg := true,
    SbtPgp.autoImport.useGpgAgent := true,
    sbtrelease.ReleasePlugin.autoImport.releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    sbtrelease.ReleasePlugin.autoImport.releaseCrossBuild := true,
    scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
    javacOptions := Seq("-source", "1.8", "-target", "1.8"),
    libraryDependencies ++= Seq(
      "org.elasticsearch"             %  "elasticsearch"        % ElasticsearchVersion,
      "com.typesafe.scala-logging"    %% "scala-logging"        % ScalaLoggingVersion,
      "commons-io"                    %  "commons-io"           % "2.4"                 % "test",
      "log4j"                         %  "log4j"                % "1.2.17"              % "test",
      "org.slf4j"                     %  "log4j-over-slf4j"     % Slf4jVersion          % "test",
      "org.mockito"                   %  "mockito-all"          % MockitoVersion        % "test",
      "org.scalatest"                 %% "scalatest"            % ScalatestVersion      % "test",
      "org.codehaus.groovy"           %  "groovy"               % "2.3.7"               % "test"

    ),
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (version.value.trim.endsWith("SNAPSHOT"))
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
