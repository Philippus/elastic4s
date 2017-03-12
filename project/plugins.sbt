resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.3")

// documentation
addSbtPlugin("com.fortysevendeg"  % "sbt-microsites" % "0.4.0")
addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.4.0")
