scalaVersion := "2.11.7"

Test / unmanagedResourceDirectories += baseDirectory.value / "target/web/public/test"

resolvers ++= Seq(
  DefaultMavenRepository,
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  "Sonatype OSS" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
)

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test,
  "com.sksamuel.elastic4s" %% "elastic4s-core" % "5.4.2",
  "com.sksamuel.elastic4s" %% "elastic4s-tcp" % "5.4.2",
  "com.sksamuel.elastic4s" %% "elastic4s-http" % "5.4.2"
)
