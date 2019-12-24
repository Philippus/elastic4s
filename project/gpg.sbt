resolvers += Classpaths.sbtPluginReleases

resolvers += Resolver.bintrayRepo("sbt", "sbt-plugin-releases")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "2.0.1")
