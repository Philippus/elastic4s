import com.typesafe.sbt.SbtPgp
import com.typesafe.sbt.pgp.PgpKeys
import sbt._
import sbt.plugins.JvmPlugin
import sbt.Keys._

object Build extends AutoPlugin {

  override def trigger  = AllRequirements
  override def requires = JvmPlugin

  object autoImport {
    val org                    = "com.sksamuel.elastic4s"
    val AkkaVersion            = "2.5.22"
    val AkkaHttpVersion        = "10.1.7"
    val CatsVersion            = "1.4.0"
    val CatsEffectVersion      = "1.2.0"
    val CirceVersion           = "0.11.1"
    val CommonsIoVersion       = "2.6"
    val ElasticsearchVersion   = "7.0.0"
    val ExtsVersion            = "1.61.0"
    val JacksonVersion         = "2.9.8"
    val Json4sVersion          = "3.6.5"
    val AWSJavaSdkVersion      = "2.5.8"
    val Log4jVersion           = "2.11.1"
    val MockitoVersion         = "1.10.19"
    val MonixVersion           = "2.3.3"
    val PlayJsonVersion        = "2.7.3"
    val ReactiveStreamsVersion = "1.0.2"
    val ScalatestVersion       = "3.0.7"
    val ScalamockVersion       = "4.1.0"
    val ScalazVersion          = "7.2.27"
    val SprayJsonVersion       = "1.3.5"
    val SttpVersion            = "1.5.12"
    val Slf4jVersion           = "1.7.26"
  }

  import autoImport._

  override def projectSettings = Seq(
    organization := org,
    scalaVersion := "2.12.8",
    crossScalaVersions := Seq("2.11.12", "2.12.8"),
    publishMavenStyle := true,
    resolvers += Resolver.mavenLocal,
    resolvers += Resolver.url("https://artifacts.elastic.co/maven"),
    javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled"),
    publishArtifact in Test := false,
    fork in Test:= false,
    parallelExecution in ThisBuild := false,
    SbtPgp.autoImport.useGpg := true,
    SbtPgp.autoImport.useGpgAgent := true,
    sbtrelease.ReleasePlugin.autoImport.releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    sbtrelease.ReleasePlugin.autoImport.releaseCrossBuild := true,
    credentials += Credentials(Path.userHome / ".sbt" / "pgp.credentials"),
    scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
    javacOptions := Seq("-source", "1.8", "-target", "1.8"),
    libraryDependencies ++= Seq(
      "com.sksamuel.exts" %% "exts"       % ExtsVersion,
      "org.slf4j"         % "slf4j-api"   % Slf4jVersion,
      "org.mockito"       % "mockito-all" % MockitoVersion % "test",
      "org.scalatest"     %% "scalatest"  % ScalatestVersion % "test"
    ),
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (version.value.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra :=
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
  )
}
