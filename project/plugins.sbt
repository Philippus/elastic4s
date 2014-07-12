resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "0.99.5.1")

addSbtPlugin("io.jenner" %% "sbt-jenner" % "0.4.0")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-pgp" % "0.8")

