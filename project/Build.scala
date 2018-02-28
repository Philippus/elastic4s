import com.typesafe.sbt.SbtPgp
import com.typesafe.sbt.pgp.PgpKeys
import sbt._
import sbt.plugins.JvmPlugin
import sbt.Keys._
import sbtrelease.ReleasePlugin

object Build extends AutoPlugin {

  override def trigger  = AllRequirements
  override def requires = ReleasePlugin

  object autoImport {
    val org                    = "com.sksamuel.elastic4s"
    val AkkaVersion            = "2.4.20"
    val CatsVersion            = "1.0.1"
    val CirceVersion           = "0.9.0"
    val CommonsIoVersion       = "2.4"
    val ElasticsearchVersion   = "6.2.2"
    val ExtsVersion            = "1.60.0"
    val JacksonVersion         = "2.9.2"
    val Json4sVersion          = "3.5.3"
    val SprayJsonVersion       = "1.3.4"
    val AWSJavaSdkVersion      = "1.11.258"
    val Log4jVersion           = "2.9.1"
    val LuceneVersion          = "7.1.0"
    val MockitoVersion         = "1.9.5"
    val PlayJsonVersion        = "2.6.8"
    val ReactiveStreamsVersion = "1.0.2"
    val ScalatestVersion       = "3.0.4"
    val Slf4jVersion           = "1.7.25"
  }

  import autoImport._

  val publishingSettings = Seq(
    publishMavenStyle := true,
    publishArtifact in Test := false,
    SbtPgp.autoImport.useGpg := true,
    SbtPgp.autoImport.useGpgAgent := true,
    sbtrelease.ReleasePlugin.autoImport.releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    sbtrelease.ReleasePlugin.autoImport.releaseCrossBuild := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value) {
        Some("snapshots" at s"${nexus}content/repositories/snapshots")
      } else {
        Some("releases" at s"${nexus}service/local/staging/deploy/maven2")
      }
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

  override def projectSettings = publishingSettings ++ Seq(
    organization := org,
    scalaVersion := "2.11.12",
    crossScalaVersions := Seq("2.11.12", "2.12.4"),
    resolvers += Resolver.mavenLocal,
    resolvers += Resolver.url("https://artifacts.elastic.co/maven"),
    javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled"),
    fork := false,
    ThisBuild / parallelExecution in ThisBuild := false,
    Global / concurrentRestrictions += Tags.limit(Tags.Test, 1),
    scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
    javacOptions := Seq("-source", "1.8", "-target", "1.8"),
    libraryDependencies ++= Seq(
      "com.sksamuel.exts" %% "exts"       % ExtsVersion,
      "org.typelevel"     %% "cats-core"  % CatsVersion,
      "org.slf4j"         % "slf4j-api"   % Slf4jVersion,
      "org.mockito"       % "mockito-all" % MockitoVersion % "test",
      "org.scalatest"     %% "scalatest"  % ScalatestVersion % "test"
    )
  )
}
