package com.sksamuel.elastic4s2.jackson

import com.sksamuel.elastic4s2.testkit.ElasticSugar
import org.scalatest.FlatSpec
import org.scalatest.mockito.MockitoSugar

class SourceTest extends FlatSpec with MockitoSugar with ElasticSugar {

  import com.sksamuel.elastic4s2.ElasticDsl._

  case class Band(name: String, albums: Seq[String], label: String)
  val band = Band("coldplay", Seq("X&Y", "Parachutes"), "Parlophone")

  client.execute {
    indexInto("music" / "bands").doc(ObjectSource(band))
  }
}
