import sbt._
import sbt.Keys._

object Build extends Build {

  val org = "com.sksamuel.elastic4s"
  val appVersion = "1.6.1"

  val ScalaVersion =          "2.11.7"
  val ScalatestVersion =      "2.2.5"
  val MockitoVersion =        "1.9.5"
  val JacksonVersion =        "2.5.3"
  val Slf4jVersion =          "1.7.12"
  val ScalaLoggingVersion =   "2.1.2"
  val ElasticsearchVersion =  "1.6.0"
  val Log4jVersion          = "1.2.17"
  val CommonsIoVersion =      "2.4"
  val GroovyVersion =         "2.3.7"

  val rootSettings = Seq(
    version := appVersion,
    organization := org,
    scalaVersion := ScalaVersion,
    crossScalaVersions := Seq("2.11.7", "2.10.5", "2.12.0-M1"),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    parallelExecution in Test := false,
    scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
    javacOptions := Seq("-source", "1.7", "-target", "1.7"),
    libraryDependencies ++= Seq(
      "org.elasticsearch"             %  "elasticsearch"        % ElasticsearchVersion,
      "org.slf4j"                     %  "slf4j-api"            % Slf4jVersion,
      "commons-io"                    %  "commons-io"           % CommonsIoVersion      % "test",
      "log4j"                         %  "log4j"                % Log4jVersion          % "test",
      "org.slf4j"                     %  "log4j-over-slf4j"     % Slf4jVersion          % "test",
      "org.mockito"                   %  "mockito-all"          % MockitoVersion        % "test",
      "org.scalatest"                 %% "scalatest"            % ScalatestVersion      % "test",
      "org.codehaus.groovy"           %  "groovy"               % GroovyVersion         % "test"

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

  lazy val root = Project("elastic4s", file("."))
    .settings(rootSettings: _*)
    .settings(publish := {})
    .settings(publishArtifact := false)
    .settings(name := "elastic4s")
    .aggregate(
      core,
      testkit,
      examples,
      jackson
    )

  lazy val core = Project("elastic4s-core", file("elastic4s-core"))
    .settings(rootSettings: _*)
    .settings(
      name := "elastic4s-core",
      libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core"            % JacksonVersion % "test",
      libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind"        % JacksonVersion % "test",
      libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonVersion % "test" exclude("org.scala-lang", "scala-library")
    )

  lazy val testkit = Project("elastic4s-testkit", file("elastic4s-testkit"))
    .settings(rootSettings: _*)
    .settings(name := "elastic4s-testkit")
    .dependsOn(core)

  lazy val examples = Project("elastic4s-examples", file("elastic4s-examples"))
    .settings(rootSettings: _*)
    .settings(name := "elastic4s-examples")
    .dependsOn(core)

  lazy val jackson = Project("elastic4s-jackson", file("elastic4s-jackson"))
    .settings(rootSettings: _*)
    .settings(
      name := "elastic4s-jackson",
      libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core"            % JacksonVersion,
      libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind"        % JacksonVersion,
      libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonVersion exclude("org.scala-lang", "scala-library")
    ).dependsOn(core, testkit % "test")
}
