import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import scalariform.formatter.preferences._

name := "elastic4s"

organization := "com.sksamuel.elastic4s"

version := "1.4.0-SNAPSHOT"

scalaVersion := "2.11.2"

crossScalaVersions := Seq("2.10.4", "2.11.2")

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

publishMavenStyle := true

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

publishTo <<= version {
  (v: String) =>
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

parallelExecution in Test := false

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oDF")

instrumentSettings

jennerProjectId := "7b0a65b8-7c9a-4f25-864c-3f3e009752b9"

jennerApiKey := "04ba7a59-5bad-4862-9bc2-a7e1aa0b93ab"

libraryDependencies ++= Seq(
  "org.elasticsearch"              %  "elasticsearch"               % "1.4.0.Beta1",
  "org.slf4j"                      %  "slf4j-api"                   % "1.7.7",
  "commons-io"                     %  "commons-io"                  % "2.4",
  "com.fasterxml.jackson.core"     %  "jackson-core"                % "2.4.2"  % "optional" ,
  "com.fasterxml.jackson.core"     %  "jackson-databind"            % "2.4.2"  % "optional" ,
  "com.fasterxml.jackson.module"   %% "jackson-module-scala"        % "2.4.2"  % "optional"  exclude ("org.scalatest", "scalatest_2.10.0"),
  "log4j"                          %  "log4j"                       % "1.2.17" % "test",
  "org.slf4j"                      %  "log4j-over-slf4j"            % "1.7.7"  % "test",
  "org.mockito"                    %  "mockito-all"                 % "1.9.5"  % "test",
  "org.scalatest"                  %% "scalatest"                   % "2.2.1"  % "test",
  "org.codehaus.groovy"            % "groovy"                       % "2.3.7"  % "test"
)

scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignParameters, true)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(MultilineScaladocCommentsStartOnFirstLine, true)
  .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)

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
      <developer>
        <id>fehmicansaglam</id>
        <name>fehmicansaglam</name>
        <url>http://github.com/fehmicansaglam</url>
      </developer>
    </developers>
}
