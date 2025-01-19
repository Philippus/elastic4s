import Dependencies._

def isGithubActions = sys.env.getOrElse("CI", "false") == "true"

// set by github actions when executing a release build
def releaseVersion: String = sys.env.getOrElse("RELEASE_VERSION", "")
def isRelease = releaseVersion != ""

// set by github actions and used as the snapshot build number
def githubRunNumber = sys.env.getOrElse("GITHUB_RUN_NUMBER", "local")

lazy val commonScalaVersionSettings = Seq(
  scalaVersion := "2.12.20",
  crossScalaVersions := Seq("2.12.20", "2.13.16")
)

lazy val warnUnusedImport = Seq(
  scalacOptions ++= Seq("-Ywarn-unused:imports"),
  Compile / console / scalacOptions ~= {
    _.filterNot(Set("-Ywarn-unused-import", "-Ywarn-unused:imports"))
  },
  Test / console / scalacOptions := (Compile / console / scalacOptions).value,
)

lazy val commonSettings = Seq(
  organization := "nl.gn0s1s",
  resolvers ++= Seq(Resolver.mavenLocal),
  Test / parallelExecution := false,
  Compile / doc / scalacOptions := (Compile / doc / scalacOptions).value.filter(_ != "-Xfatal-warnings"),
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8")
)

lazy val publishSettings = Seq(
  Test / publishArtifact := false,
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
  startYear := Some(2013),
  homepage := Some(url("https://github.com/philippus/elastic4s")),
  licenses += License.Apache2,
  developers := List(
    Developer(
      id = "Philippus",
      name = "Philippus Baalman",
      email = "",
      url = url("https://github.com/philippus")
    ),
    Developer(
      id = "sksamuel",
      name = "Samuel",
      email = "",
      url = url("https://github.com/sksamuel")
    )
  )
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
  .settings(libraryDependencies ++= Seq(Dependencies.scalaTestMain, scalaTestPlusMockito))

lazy val httpstreams = (project in file("elastic4s-http-streams"))
  .dependsOn(core, testkit % "test", jackson % "test")
  .settings(name := "elastic4s-http-streams")
  .settings(allSettings)
  .settings(libraryDependencies ++=
    Seq(
      Dependencies.akkaActor,
      Dependencies.akkaStream,
      Dependencies.reactiveStreamsTck,
      Dependencies.scalaTestPlusTestng
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
      "org.apache.logging.log4j" % "log4j-api" % "2.24.3" % "test",
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.24.3" % "test",
      "org.apache.logging.log4j" % "log4j-core" % "2.24.3" % "test"
    ),
    Test / fork := false,
    Test / parallelExecution := false,
    Test / testForkedParallel := false
  )
