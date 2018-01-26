resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.7")

// documentation
addSbtPlugin("com.fortysevendeg"  % "sbt-microsites" % "0.4.0")
addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.4.0")
