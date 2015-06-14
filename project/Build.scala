import sbt._
import sbt.Keys._

object Build extends Build {

  val org = "com.sksamuel.elastic4s"
  val appVersion = "1.5.14"

  val ScalaVersion =          "2.11.6"
  val ScalatestVersion =      "2.2.5"
  val MockitoVersion =        "1.9.5"
  val JacksonVersion =        "2.5.2"
  val Slf4jVersion =          "1.7.7"
  val ScalaLoggingVersion =   "2.1.2"
  val ElasticsearchVersion =  "1.5.2"

  val rootSettings = Seq(
    version := appVersion,
    organization := org,
    scalaVersion := ScalaVersion,
    crossScalaVersions := Seq("2.11.6", "2.10.5"),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    parallelExecution in Test := false,
    scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
    javacOptions := Seq("-source", "1.7", "-target", "1.7"),
    libraryDependencies ++= Seq(
      "org.elasticsearch"             %  "elasticsearch"        % ElasticsearchVersion,
      "com.typesafe.scala-logging"    %% "scala-logging-slf4j"  % ScalaLoggingVersion,
      "commons-io"                    %  "commons-io"           % "2.4"                 % "test",
      "log4j"                         %  "log4j"                % "1.2.17"              % "test",
      "org.slf4j"                     %  "log4j-over-slf4j"     % Slf4jVersion          % "test",
      "org.mockito"                   %  "mockito-all"          % MockitoVersion        % "test",
      "org.scalatest"                 %% "scalatest"            % ScalatestVersion      % "test",
      "org.codehaus.groovy"           %  "groovy"               % "2.3.7"               % "test"

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
    .settings(name := "elastic4s")
    .aggregate(
      core,
      testkit,
      jackson
    )

  lazy val core = Project("elastic4s-core", file("elastic4s-core"))
    .settings(rootSettings: _*)
    .settings(
      name := "elastic4s",
      libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core"            % JacksonVersion % "test",
      libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind"        % JacksonVersion % "test",
      libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonVersion % "test" exclude("org.scala-lang", "scala-library")
    )

  lazy val testkit = Project("elastic4s-testkit", file("elastic4s-testkit"))
    .settings(rootSettings: _*)
    .settings(name := "elastic4s-testkit")
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
