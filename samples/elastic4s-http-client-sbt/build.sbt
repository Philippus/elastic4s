lazy val root = Project("elastic4s-http-example", file("."))
  .settings(name := "elastic4s-http-example")
  .settings(scalaVersion := "2.12.2")
  .settings(libraryDependencies ++= Seq(
    "com.sksamuel.elastic4s" %% "elastic4s-embedded" % "5.4.0",
    "com.sksamuel.elastic4s" %% "elastic4s-http" % "5.4.0"
  ))
