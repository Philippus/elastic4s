package com.sksamuel.elastic4s2.jackson

import com.sksamuel.elastic4s2.ElasticDsl2$
import com.sksamuel.elastic4s2.testkit.ElasticSugar
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar

class SourceTest extends FlatSpec with MockitoSugar with ElasticSugar with ElasticDsl2 {

  case class Band(name: String, albums: Seq[String], label: String)
  val band = Band("coldplay", Seq("X&Y", "Parachutes"), "Parlophone")

  client.execute {
    index into "music/bands" doc ObjectSource(band)
  }
}
