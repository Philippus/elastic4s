
name := "elastic4s"

organization := "com.sksamuel.elastic4s"

version := "0.90.13.2"

scalaVersion  := "2.11.2"

crossScalaVersions := Seq("2.10.4", "2.11.2")

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

publishMavenStyle := true

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

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

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.1.3"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.1.3"

libraryDependencies += "com.fasterxml.jackson.jaxrs" % "jackson-jaxrs-json-provider" % "2.1.3"

libraryDependencies += "com.fasterxml.jackson.datatype" % "jackson-datatype-hibernate4" % "2.1.2"

libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.4.1"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.6.6"

libraryDependencies += "log4j" % "log4j" % "1.2.17" % "test"

libraryDependencies += "org.slf4j" % "log4j-over-slf4j" % "1.6.6" % "test"

libraryDependencies += "commons-io" % "commons-io" % "2.4"

libraryDependencies += "org.mockito" % "mockito-all" % "1.9.5" % "test"

libraryDependencies += "org.elasticsearch" % "elasticsearch" % "0.90.13"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

ScoverageSbtPlugin.instrumentSettings

CoverallsPlugin.singleProject

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
        <url>http://github.com/elastic4s</url>
      </developer>
    </developers>
}
