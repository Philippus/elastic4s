scalaVersion := "2.11.7"

libraryDependencies ++= Seq( jdbc , cache , ws   , specs2 % Test )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += DefaultMavenRepository

resolvers += "Sonatype OSS" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"

libraryDependencies ++= Seq(
  "com.sksamuel.elastic4s" %% "elastic4s-core" % "5.4.2",
  "com.sksamuel.elastic4s" %% "elastic4s-tcp" % "5.4.2",
  "com.sksamuel.elastic4s" %% "elastic4s-http" % "5.4.2"
)