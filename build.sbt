
val isTravis = settingKey[Boolean]("Flag indicating whether the current build is running under Travis")
isTravis in Global := sys.env.get("TRAVIS").isDefined

val travisBuildNumber = settingKey[String]("Value of the travis build number")
travisBuildNumber in Global := sys.env.getOrElse("TRAVIS_BUILD_NUMBER", "0")

val org                    = "com.sksamuel.elastic4s"
val AkkaVersion            = "2.6.10"
val AkkaHttpVersion        = "10.1.12"
val CatsVersion            = "2.0.0"
val CatsEffectVersion      = "2.1.4"
val CirceVersion           = "0.13.0"
val CommonsIoVersion       = "2.8.0"
val ElasticsearchVersion   = "7.9.3"
val ExtsVersion            = "1.61.1"
val JacksonVersion         = "2.11.3"
val Json4sVersion          = "3.6.10"
val Log4jVersion           = "2.14.0"
val MockitoVersion         = "3.6.0"
val MonixVersion           = "3.1.0"
val PlayJsonVersion        = "2.9.1"
val ReactiveStreamsVersion = "1.0.3"
val ScalatestVersion       = "3.1.4"
val ScalatestPlusVersion   = "3.1.2.0"
val ScalamockVersion       = "4.4.0"
val ScalazVersion          = "7.2.30"
val ZIOVersion             = "1.0.3"
val SprayJsonVersion       = "1.3.5"
val SttpVersion            = "1.7.2"
val Slf4jVersion           = "1.7.30"
val ScalatestPlusMockitoArtifactId = "mockito-3-2"

lazy val commonScalaVersionSettings = Seq(
  scalaVersion := "2.12.12",
  crossScalaVersions := Seq("2.12.12", "2.13.3")
)

lazy val warnUnusedImport = Seq(
  scalacOptions ++= Seq("-Ywarn-unused:imports"),
  scalacOptions in(Compile, console) ~= {
    _.filterNot(Set("-Ywarn-unused-import", "-Ywarn-unused:imports"))
  },
  scalacOptions in(Test, console) := (scalacOptions in(Compile, console)).value,
)

lazy val commonSettings = Seq(
  organization := "com.sksamuel.elastic4s",
  version := (if (isTravis.value) version.value.stripSuffix("-SNAPSHOT") + s".${travisBuildNumber.value}-SNAPSHOT" else version.value),
  resolvers ++= Seq(Resolver.mavenLocal),
  parallelExecution in Test := false,
  scalacOptions in(Compile, doc) := (scalacOptions in(Compile, doc)).value.filter(_ != "-Xfatal-warnings"),
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8")
)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := Function.const(false),
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isTravis.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  }
)

lazy val commonJvmSettings = Seq(
  testOptions in Test += {
    val flag = if ((isTravis in Global).value) "-oCI" else "-oDF"
    Tests.Argument(TestFrameworks.ScalaTest, flag)
  },
  Test / fork := true,
  Test / javaOptions := Seq("-Xmx3G"),
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:+CMSClassUnloadingEnabled"),
)

lazy val commonDeps = Seq(
  libraryDependencies ++= Seq(
    "com.sksamuel.exts" %% "exts" % ExtsVersion,
    "org.slf4j" % "slf4j-api" % Slf4jVersion,
    "org.scalatest" %% "scalatest" % ScalatestVersion % "test",
    "org.mockito" % "mockito-core" % MockitoVersion % "test",
    "org.scalatestplus" %% ScalatestPlusMockitoArtifactId % ScalatestPlusVersion % "test"
  )
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

val travisCreds = Credentials(
  "Sonatype Nexus Repository Manager",
  "oss.sonatype.org",
  sys.env.getOrElse("OSSRH_USERNAME", ""),
  sys.env.getOrElse("OSSRH_PASSWORD", "")
)

val localCreds = Credentials(Path.userHome / ".sbt" / "credentials.sbt")

lazy val credentialSettings = Seq(
  credentials := (if (isTravis.value) Seq(travisCreds) else Seq(localCreds))
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
    core,
    clientesjava,
    clientsSniffed,
    cats_effect,
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
    clientsttp,
    clientakka,
    httpstreams,
    akkastreams
  )

lazy val core = (project in file("elastic4s-core"))
  .settings(name := "elastic4s-core")
  .settings(allSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-core" % JacksonVersion,
      "com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion,
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonVersion
    )
  )

lazy val clientesjava = (project in file("elastic4s-client-esjava"))
  .dependsOn(core)
  .settings(name := "elastic4s-client-esjava")
  .settings(allSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.elasticsearch.client" % "elasticsearch-rest-client" % ElasticsearchVersion,
      "org.apache.logging.log4j" % "log4j-api" % Log4jVersion % "test",
      "com.fasterxml.jackson.core" % "jackson-core" % JacksonVersion,
      "com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion,
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonVersion exclude("org.scala-lang", "scala-library")
    )
  )

lazy val clientsSniffed = (project in file("elastic4s-client-sniffed"))
  .dependsOn(clientesjava)
  .settings(name := "elastic4s-client-sniffed")
  .settings(allSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.elasticsearch.client" % "elasticsearch-rest-client-sniffer" % ElasticsearchVersion,
    )
  )

lazy val cats_effect = (project in file("elastic4s-effect-cats"))
  .dependsOn(core)
  .settings(name := "elastic4s-effect-cats")
  .settings(allSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % CatsEffectVersion
    )
  )

lazy val zio = (project in file("elastic4s-effect-zio"))
  .dependsOn(core, testkit % "test")
  .settings(name := "elastic4s-effect-zio")
  .settings(allSettings)
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % ZIOVersion
    )
  )

lazy val scalaz = (project in file("elastic4s-effect-scalaz"))
  .dependsOn(core)
  .settings(name := "elastic4s-effect-scalaz")
  .settings(allSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalaz" %% "scalaz-core" % ScalazVersion,
      "org.scalaz" %% "scalaz-concurrent" % ScalazVersion
    )
  )

lazy val monix = (project in file("elastic4s-effect-monix"))
  .dependsOn(core)
  .settings(name := "elastic4s-effect-monix")
  .settings(allSettings)
  .settings(
    libraryDependencies ++= Seq(
      "io.monix" %% "monix" % MonixVersion
    )
  )

lazy val testkit = (project in file("elastic4s-testkit"))
  .dependsOn(core, clientesjava)
  .settings(name := "elastic4s-testkit")
  .settings(allSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % ScalatestVersion,
      "org.scalatestplus" %% ScalatestPlusMockitoArtifactId % ScalatestPlusVersion
    )
  )

lazy val httpstreams = (project in file("elastic4s-http-streams"))
  .dependsOn(core, testkit % "test", jackson % "test")
  .settings(name := "elastic4s-http-streams")
  .settings(allSettings)
  .settings(
    libraryDependencies += "com.typesafe.akka"   %% "akka-actor"          % AkkaVersion,
    libraryDependencies += "org.reactivestreams" % "reactive-streams"     % ReactiveStreamsVersion,
    libraryDependencies += "org.reactivestreams" % "reactive-streams-tck" % ReactiveStreamsVersion % "test",
    libraryDependencies += "org.scalatestplus" %% "testng-6-7" % ScalatestPlusVersion % "test"
  )

lazy val akkastreams = (project in file("elastic4s-streams-akka"))
  .dependsOn(core, testkit % "test", jackson % "test")
  .settings(name := "elastic4s-streams-akka")
  .settings(allSettings)
  .settings(
    libraryDependencies += "com.typesafe.akka" %% "akka-stream" % AkkaVersion
  )

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
  .settings(
    libraryDependencies += "io.circe" %% "circe-core" % CirceVersion,
    libraryDependencies += "io.circe" %% "circe-generic" % CirceVersion,
    libraryDependencies += "io.circe" %% "circe-parser" % CirceVersion
  )

lazy val json4s = (project in file("elastic4s-json-json4s"))
  .dependsOn(core)
  .settings(name := "elastic4s-json-json4s")
  .settings(allSettings)
  .settings(
    libraryDependencies += "org.json4s" %% "json4s-core" % Json4sVersion,
    libraryDependencies += "org.json4s" %% "json4s-jackson" % Json4sVersion
  )

lazy val playjson = (project in file("elastic4s-json-play"))
  .dependsOn(core)
  .settings(name := "elastic4s-json-play")
  .settings(allSettings)
  .settings(
    libraryDependencies += "com.typesafe.play" %% "play-json" % PlayJsonVersion
  )

lazy val sprayjson = (project in file("elastic4s-json-spray"))
  .dependsOn(core)
  .settings(name := "elastic4s-json-spray")
  .settings(allSettings)
  .settings(
    libraryDependencies += "io.spray" %% "spray-json" % SprayJsonVersion
  )

lazy val clientsttp = (project in file("elastic4s-client-sttp"))
  .dependsOn(core)
  .settings(name := "elastic4s-client-sttp")
  .settings(allSettings)
  .settings(
    libraryDependencies += "com.softwaremill.sttp" %% "core" % SttpVersion,
    libraryDependencies += "com.softwaremill.sttp" %% "async-http-client-backend-future" % SttpVersion
  )

lazy val clientakka = (project in file("elastic4s-client-akka"))
  .dependsOn(core, testkit % "test")
  .settings(name := "elastic4s-client-akka")
  .settings(allSettings)
  .settings(
    libraryDependencies += "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    libraryDependencies += "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    libraryDependencies += "org.scalamock" %% "scalamock" % ScalamockVersion % "test"
  )

lazy val tests = (project in file("elastic4s-tests"))
  .settings(name := "elastic4s-tests")
  .dependsOn(core, jackson, testkit % "test")
  .settings(allSettings)
  .settings(noPublishSettings)
  .settings(
    libraryDependencies ++= Seq(
      "commons-io" % "commons-io" % CommonsIoVersion % "test",
      "org.mockito" % "mockito-core" % MockitoVersion % "test",
      "com.fasterxml.jackson.core" % "jackson-core" % JacksonVersion % "test",
      "com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion % "test",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonVersion % "test" exclude("org.scala-lang", "scala-library"),
      "org.apache.logging.log4j" % "log4j-api" % "2.12.0" % "test",
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.12.0" % "test",
      "org.apache.logging.log4j" % "log4j-core" % "2.12.0" % "test"
    ),
    fork in Test := false,
    parallelExecution in Test := false,
    testForkedParallel in Test := false
  )
