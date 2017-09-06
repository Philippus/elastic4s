import com.typesafe.sbt.pgp.PgpKeys
import sbt._
import sbt.plugins.JvmPlugin
import sbt.Keys._

object Build extends AutoPlugin {

  override def trigger = AllRequirements
  override def requires = JvmPlugin

  object autoImport {
    val org = "com.sksamuel.elastic4s"
    val AkkaVersion = "2.3.16"
    val CatsVersion = "1.0.0-MF"
    val CirceVersion = "0.9.0-M1"
    val CommonsIoVersion = "2.4"
    val ElasticsearchVersion = "6.0.0-beta2"
    val ExtsVersion = "1.52.0"
    val JacksonVersion = "2.8.9"
    val Json4sVersion = "3.5.3"
    val SprayJsonVersion = "1.3.3"
    val Log4jVersion = "2.6.2"
    val LuceneVersion = "7.0.0-snapshot-00142c9"
    val MockitoVersion = "1.9.5"
    val PlayJsonVersion = "2.6.3"
    val ReactiveStreamsVersion = "1.0.0"
    val ScalatestVersion = "3.0.4"
    val Slf4jVersion = "1.7.12"
  }

  import autoImport._

  override def projectSettings = Seq(
    organization := org,
    // a 'compileonly' configuation
    ivyConfigurations += config("compileonly").hide,
    // appending everything from 'compileonly' to unmanagedClasspath
    unmanagedClasspath in Compile ++= update.value.select(configurationFilter("compileonly")),
    scalaVersion := "2.10.6",
    crossScalaVersions := Seq("2.10.6", "2.11.8", "2.12.2"),
    publishMavenStyle := true,
    resolvers += Resolver.mavenLocal,
    resolvers += "Elasticsearch Lucene Snapshots" at "https://download.elasticsearch.org/lucenesnapshots/00142c9",
    javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled"),
    publishArtifact in Test := false,
    fork := false,
    parallelExecution := false,
    parallelExecution in ThisBuild := false,
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    sbtrelease.ReleasePlugin.autoImport.releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    sbtrelease.ReleasePlugin.autoImport.releaseCrossBuild := true,
    scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
    javacOptions := Seq("-source", "1.7", "-target", "1.7"),
    libraryDependencies ++= Seq(
      "com.sksamuel.exts"                     %% "exts"                     % ExtsVersion,
      "org.typelevel"                         %% "cats-core"                % CatsVersion,
      "org.slf4j"                             % "slf4j-api"                 % Slf4jVersion,
      "org.mockito"                           % "mockito-all"               % MockitoVersion        % "test",
      "org.scalatest"                         %% "scalatest"                % ScalatestVersion      % "test"
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
