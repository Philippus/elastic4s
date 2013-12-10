resolvers += Classpaths.sbtPluginReleases

//This line enables the sbt idea plugin
addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.2.0")

//This line enables the sbt eclipse plugin
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.1.1")

addSbtPlugin("com.sksamuel.scoverage" %% "sbt-scoverage" % "0.95.1")

addSbtPlugin("com.sksamuel.scoverage" %% "sbt-coveralls" % "0.0.5")