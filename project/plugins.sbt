resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.7")

// documentation
addSbtPlugin("com.47deg"  % "sbt-microsites" % "0.7.14")
addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.4.1")
