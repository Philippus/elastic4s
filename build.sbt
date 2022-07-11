import Dependencies._

ThisBuild / organizationName := "com.sksamuel.elastic4s"

def isGithubActions = sys.env.getOrElse("CI", "false") == "true"

// set by github actions when executing a release build
def releaseVersion: String = sys.env.getOrElse("RELEASE_VERSION", "")
def isRelease = releaseVersion != ""

// the version to use to publish - either from release version or a snapshot run number
def publishVersion = if (isRelease) releaseVersion else "7.17.0." + githubRunNumber + "-SNAPSHOT"

// set by github actions and used as the snapshot build number
def githubRunNumber = sys.env.getOrElse("GITHUB_RUN_NUMBER", "local")

// creds for release to maven central
def ossrhUsername = sys.env.getOrElse("OSSRH_USERNAME", "")
def ossrhPassword = sys.env.getOrElse("OSSRH_PASSWORD", "")


lazy val commonScalaVersionSettings = Seq(
  scalaVersion := "2.12.16",
  crossScalaVersions := Seq("2.12.16", "2.13.8")
)

lazy val warnUnusedImport = Seq(
  scalacOptions ++= Seq("-Ywarn-unused:imports"),
  Compile / console / scalacOptions ~= {
    _.filterNot(Set("-Ywarn-unused-import", "-Ywarn-unused:imports"))
  },
  Test / console / scalacOptions := (Compile / console / scalacOptions).value,
)

lazy val commonSettings = Seq(
  organization := "com.sksamuel.elastic4s",
  version := publishVersion,
  resolvers ++= Seq(Resolver.mavenLocal),
  Test / parallelExecution := false,
  Compile / doc / scalacOptions := (Compile / doc / scalacOptions).value.filter(_ != "-Xfatal-warnings"),
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8")
)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  Test / publishArtifact := false,
  pomIncludeRepository := Function.const(false),
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isRelease)
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
    else
      Some("snapshots" at nexus + "content/repositories/snapshots")
  }
)

lazy val commonJvmSettings = Seq(
   Test / testOptions += {
    val flag = if (isGithubActions) "-oCI" else "-oDF"
    Tests.Argument(TestFrameworks.ScalaTest, flag)
  },
  Test / fork := true,
  Test / javaOptions := Seq("-Xmx3G"),
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:+CMSClassUnloadingEnabled"),
)


lazy val pomSettings = Seq(
  homepage := Some(url("https://github.com/sksamuel/elastic4s")),
  licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  scmInfo := Some(ScmInfo(url("https://github.com/sksamuel/elastic4s"), "scm:git:git@github.com:sksamuel/elastic4s.git")),
  apiURL := Some(url("http://github.com/sksamuel/elastic4s/")),
  pomExtra := <developers>
    <developer>
      <id>sksamuel</id>
      <name>Sam Samuel</name>
      <url>https://github.com/sksamuel</url>
    </developer>
  </developers>
)

lazy val credentialSettings = Seq(
  credentials := Seq(Credentials(
    "Sonatype Nexus Repository Manager",
    "oss.sonatype.org",
    sys.env.getOrElse("OSSRH_USERNAME", ""),
    sys.env.getOrElse("OSSRH_PASSWORD", "")
  ))
)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)


lazy val allSettings = commonScalaVersionSettings ++
  commonJvmSettings ++
  commonSettings ++
  commonDeps ++
  credentialSettings ++
  pomSettings ++
  warnUnusedImport ++
  publishSettings



lazy val root = Project("elastic4s", file("."))
  .settings(name := "elastic4s")
  .settings(allSettings)
  .settings(noPublishSettings)
  .aggregate(
    json_builder,
    domain,
    handlers,
    core,
    clientcore,
    clientesjava,
    clientsSniffed,
    cats_effect,
    cats_effect_2,
    zio,
    scalaz,
    monix,
    tests,
    testkit,
    circe,
    jackson,
    json4s,
    playjson,
    sprayjson,
    ziojson,
    clientsttp,
    clientakka,
    httpstreams,
    akkastreams
  )

lazy val domain = (project in file("elastic4s-domain"))
  .settings(name := "elastic4s-domain")
  .dependsOn(json_builder)
  .settings(allSettings)
  .settings(libraryDependencies ++= fasterXmlJacksonScala)

lazy val json_builder = (project in file("elastic4s-json-builder"))
  .settings(name := "elastic4s-json-builder")
  .settings(allSettings)
  .settings(libraryDependencies ++= fasterXmlJacksonScala)

lazy val core = (project in file("elastic4s-core"))
  .settings(name := "elastic4s-core")
  .dependsOn(domain, clientcore, handlers, json_builder)
  .settings(allSettings)
  .settings(libraryDependencies ++= fasterXmlJacksonScala)

lazy val handlers = (project in file("elastic4s-handlers"))
  .settings(name := "elastic4s-handlers")
  .dependsOn(domain, json_builder)
  .settings(allSettings)
  .settings(libraryDependencies ++= fasterXmlJacksonScala)

lazy val clientcore = (project in file("elastic4s-client-core"))
  .settings(name := "elastic4s-client-core")
  .dependsOn(handlers)
  .settings(allSettings)
  .settings(libraryDependencies ++= Seq(log4jApi))

lazy val clientesjava = (project in file("elastic4s-client-esjava"))
  .settings(name := "elastic4s-client-esjava")
  .dependsOn(core)
  .settings(allSettings)
  .settings(
    libraryDependencies ++= Seq(elasticsearchRestClient,
      log4jApi,
      "com.fasterxml.jackson.core" % "jackson-core" % JacksonVersion,
      "com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion,
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonVersion exclude("org.scala-lang", "scala-library")
    )
  )

lazy val clientsSniffed = (project in file("elastic4s-client-sniffed"))
  .settings(name := "elastic4s-client-sniffed")
  .dependsOn(clientesjava)
  .settings(allSettings)
  .settings(libraryDependencies ++= Seq(elasticsearchRestClientSniffer))

lazy val cats_effect = (project in file("elastic4s-effect-cats"))
  .dependsOn(core, testkit % "test")
  .settings(name := "elastic4s-effect-cats")
  .settings(allSettings)
  .settings(libraryDependencies += cats)

lazy val cats_effect_2 = (project in file("elastic4s-effect-cats-2"))
  .dependsOn(core, testkit % "test")
  .settings(name := "elastic4s-effect-cats-2")
  .settings(allSettings)
  .settings(libraryDependencies += cats2)

lazy val zio = (project in file("elastic4s-effect-zio"))
  .dependsOn(core, testkit % "test")
  .settings(name := "elastic4s-effect-zio")
  .settings(allSettings)
  .settings(libraryDependencies ++= Dependencies.zio)

lazy val scalaz = (project in file("elastic4s-effect-scalaz"))
  .dependsOn(core)
  .settings(name := "elastic4s-effect-scalaz")
  .settings(allSettings)
  .settings(libraryDependencies ++= Dependencies.scalaz)

lazy val monix = (project in file("elastic4s-effect-monix"))
  .dependsOn(core)
  .settings(name := "elastic4s-effect-monix")
  .settings(allSettings)
  .settings(libraryDependencies += Dependencies.monix)

lazy val testkit = (project in file("elastic4s-testkit"))
  .dependsOn(core, clientesjava)
  .settings(name := "elastic4s-testkit")
  .settings(allSettings)
  .settings(libraryDependencies ++= Seq(Dependencies.scalaTest, scalaTestPlusMokito))

lazy val httpstreams = (project in file("elastic4s-http-streams"))
  .dependsOn(core, testkit % "test", jackson % "test")
  .settings(name := "elastic4s-http-streams")
  .settings(allSettings)
  .settings(libraryDependencies ++=
    Seq(
      Dependencies.akkaActor,
      Dependencies.akkaStream,
      Dependencies.reactiveStreamsTck,
      Dependencies.scalaTestPlusTestng67
    )
  )

lazy val akkastreams = (project in file("elastic4s-streams-akka"))
  .dependsOn(core, testkit % "test", jackson % "test")
  .settings(name := "elastic4s-streams-akka")
  .settings(allSettings)
  .settings(libraryDependencies += Dependencies.akkaStream)

lazy val jackson = (project in file("elastic4s-json-jackson"))
  .dependsOn(core)
  .settings(name := "elastic4s-json-jackson")
  .settings(allSettings)
  .settings(
    libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % JacksonVersion,
    libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion,
    libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonVersion exclude("org.scala-lang", "scala-library")
  )

lazy val circe = (project in file("elastic4s-json-circe"))
  .dependsOn(core)
  .settings(name := "elastic4s-json-circe")
  .settings(allSettings)
  .settings(libraryDependencies ++= Dependencies.circe)

lazy val json4s = (project in file("elastic4s-json-json4s"))
  .dependsOn(core)
  .settings(name := "elastic4s-json-json4s")
  .settings(allSettings)
  .settings(libraryDependencies ++= Dependencies.json4s)

lazy val playjson = (project in file("elastic4s-json-play"))
  .dependsOn(core)
  .settings(name := "elastic4s-json-play")
  .settings(allSettings)
  .settings(libraryDependencies ++= Dependencies.playJson)

lazy val sprayjson = (project in file("elastic4s-json-spray"))
  .dependsOn(core)
  .settings(name := "elastic4s-json-spray")
  .settings(allSettings)
  .settings(libraryDependencies ++= Dependencies.sprayJson)

lazy val ziojson = (project in file("elastic4s-json-zio"))
  .dependsOn(core)
  .settings(name := "elastic4s-json-zio")
  .settings(allSettings)
  .settings(libraryDependencies += Dependencies.zioJson)

lazy val clientsttp = (project in file("elastic4s-client-sttp"))
  .dependsOn(core)
  .settings(name := "elastic4s-client-sttp")
  .settings(allSettings)
  .settings(libraryDependencies ++= Seq(sttp, asyncHttpClientBackendFuture))

lazy val clientakka = (project in file("elastic4s-client-akka"))
  .dependsOn(core, testkit % "test")
  .settings(name := "elastic4s-client-akka")
  .settings(allSettings)
  .settings(libraryDependencies ++= Seq(akkaHTTP, akkaStream, scalaMock))

lazy val tests = (project in file("elastic4s-tests"))
  .settings(name := "elastic4s-tests")
  .dependsOn(core, jackson, testkit % "test")
  .settings(allSettings)
  .settings(noPublishSettings)
  .settings(
    libraryDependencies ++= Seq(
      commonsIo,
      mockitoCore,
      "com.fasterxml.jackson.core" % "jackson-core" % JacksonVersion % "test",
      "com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion % "test",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonVersion % "test" exclude("org.scala-lang", "scala-library"),
      "org.apache.logging.log4j" % "log4j-api" % "2.17.2" % "test",
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.17.2" % "test",
      "org.apache.logging.log4j" % "log4j-core" % "2.17.2" % "test"
    ),
    Test / fork := false,
    Test / parallelExecution := false,
    Test / testForkedParallel := false
  )
