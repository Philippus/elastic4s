lazy val root = Project("elastic4s-tcp-client-sbt", file("."))
  .settings(name := "elastic4s-tcp-client-sbt")
  .settings(scalaVersion := "2.12.2")
  .settings(libraryDependencies ++= Seq(
    "com.sksamuel.elastic4s" %% "elastic4s-tcp" % "5.4.2"
  ))
