package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{ FlatSpec, Matchers }
import scala.collection.JavaConverters._

/** @author Stephen Samuel */
class SearchTest extends FlatSpec with MockitoSugar with ElasticSugar with Matchers {

  client.execute {
    index into "music/bands" fields (
      "name" -> "coldplay",
      "singer" -> "chris martin",
      "drummer" -> "will champion",
      "guitar" -> "johnny buckland"
    )
  }
  client.execute {
    index into "music/artists" fields (
      "name" -> "kate bush",
      "singer" -> "kate bush"
    )
  }
  client.execute {
    index into "music/bands" fields (
      "name" -> "jethro tull",
      "singer" -> "ian anderson",
      "guitar" -> "martin barre",
      "keyboards" -> "johnny smith"
    ) id 45
  }

  refresh("music")
  blockUntilCount(3, "music")

  "a search query" should "find an indexed document that matches a string query" in {
    val resp = client.execute {
      search in "music" -> "bands" query "anderson"
    }.await
    assert(1 === resp.getHits.totalHits())
  }

  it should "find an indexed document in the given type only" in {
    val resp1 = client.execute {
      search in "music" -> "bands" query "kate"
    }.await
    assert(0 === resp1.getHits.totalHits())

    val resp2 = client.execute {
      search in "music" -> "artists" query "kate"
    }.await
    assert(1 === resp2.getHits.totalHits())
  }

  it should "return specified fields" in {
    val resp1 = client.execute {
      search in "music/bands" query "jethro" fields "singer"
    }.await
    assert(resp1.getHits.getHits.exists(_.getFields.get("singer").getValues.contains("ian anderson")))
  }

  it should "support source includes" in {
    val resp1 = client.execute {
      search in "music/bands" query "jethro" sourceInclude ("keyboards", "guit*")
    }.await
    val map = resp1.getHits.getHits()(0).sourceAsMap.asScala
    map.contains("keyboards") shouldBe true
    map.contains("guitar") shouldBe true
    map.contains("singer") shouldBe false
    map.contains("name") shouldBe false
  }

  it should "support source excludes" in {
    val resp1 = client.execute {
      search in "music/bands" query "jethro" sourceExclude ("na*", "guit*")
    }.await
    val map = resp1.getHits.getHits()(0).sourceAsMap.asScala
    map.contains("keyboards") shouldBe true
    map.contains("guitar") shouldBe false
    map.contains("singer") shouldBe true
    map.contains("name") shouldBe false
  }
}
