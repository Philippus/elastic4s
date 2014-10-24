package com.sksamuel.elastic4s

import org.scalatest.{ OneInstancePerTest, FlatSpec }
import org.scalatest.mock.MockitoSugar

class SourceTest extends FlatSpec with MockitoSugar with ElasticSugar with ElasticDsl {

  case class Band(name: String, albums: Seq[String], label: String)
  val band = Band("coldplay", Seq("X&Y", "Parachutes"), "Parlophone")

  client.execute {
    index into "music/bands" doc band
  }
}
