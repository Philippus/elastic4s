package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.testkit.{ElasticMatchers, ElasticSugar}
import org.scalatest.WordSpec
import org.scalatest.concurrent.Eventually

import scala.collection.JavaConverters._

/** @author Stephen Samuel */
class SearchTest
  extends WordSpec
  with ElasticSugar
  with Eventually
  with ElasticMatchers {

  client.execute {
    bulk(
      index into "musicians/bands" fields(
        "name" -> "coldplay",
        "singer" -> "chris martin",
        "drummer" -> "will champion",
        "guitar" -> "johnny buckland"
        ),
      index into "musicians/performers" fields(
        "name" -> "kate bush",
        "singer" -> "kate bush"
        ),
      index into "musicians/bands" fields(
        "name" -> "jethro tull",
        "singer" -> "ian anderson",
        "guitar" -> "martin barre",
        "keyboards" -> "johnny smith"
        ) id 45
    )
  }.await

  refresh("musicians")
  blockUntilCount(3, "musicians")

  "a search query" should {
    "find an indexed document that matches a string query" in {
      search in "musicians" -> "bands" query "anderson" should haveTotalHits(1)
    }
    "find an indexed document in the given type only" in {
      search in "musicians" -> "bands" query "kate" should haveNoHits
      search in "musicians" -> "performers" query "kate" should haveTotalHits(1)
    }
    "return specified fields" in {
      search in "musicians/bands" query "jethro" fields "singer" should includeFieldValue("ian anderson")
    }
    "support source includes" in {
      val resp1 = client.execute {
        search in "musicians/bands" query "jethro" sourceInclude ("keyboards", "guit*")
      }.await
      val map = resp1.getHits.getHits()(0).sourceAsMap.asScala
      map.contains("keyboards") shouldBe true
      map.contains("guitar") shouldBe true
      map.contains("singer") shouldBe false
      map.contains("name") shouldBe false
    }
    "support source excludes" in {
      val resp1 = client.execute {
        search in "musicians/bands" query "jethro" sourceExclude ("na*", "guit*")
      }.await
      val map = resp1.getHits.getHits()(0).sourceAsMap.asScala
      map.contains("keyboards") shouldBe true
      map.contains("guitar") shouldBe false
      map.contains("singer") shouldBe true
      map.contains("name") shouldBe false
    }
  }
}
