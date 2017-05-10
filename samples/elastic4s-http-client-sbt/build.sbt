lazy val root = Project("elastic4s-http-client-sbt", file("."))
  .settings(name := "elastic4s-http-client-sbt")
  .settings(scalaVersion := "2.12.2")
  .settings(libraryDependencies ++= Seq(
    "com.sksamuel.elastic4s" %% "elastic4s-http" % "5.4.2"
  ))
