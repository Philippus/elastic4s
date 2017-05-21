package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.testkit.{ClassloaderLocalNodeProvider, ElasticMatchers}
import org.scalatest.WordSpec

class SearchTest
  extends WordSpec
    with ClassloaderLocalNodeProvider
    with ElasticMatchers
    with ElasticDsl {

  client.execute {
    bulk(
      indexInto("musicians/bands").fields(
        "name" -> "coldplay",
        "singer" -> "chris martin",
        "drummer" -> "will champion",
        "guitar" -> "johnny buckland"
      ),
      indexInto("musicians/performers").fields(
        "name" -> "kate bush",
        "singer" -> "kate bush"
      ),
      indexInto("musicians/bands").fields(
        "name" -> "jethro tull",
        "singer" -> "ian anderson",
        "guitar" -> "martin barre",
        "keyboards" -> "johnny smith"
      ) id 45
    ).immediateRefresh()
  }.await

  "a search query" should {
    "find an indexed document that matches a string query" in {
      search("musicians" -> "bands") query "anderson" should haveTotalHits(1)
    }
    "find an indexed document in the given type only" in {
      search("musicians" -> "bands") query "kate" should haveNoHits
      search("musicians" -> "performers") query "kate" should haveTotalHits(1)
    }
    "return source" in {
      search("musicians" / "bands").query("jethro") should haveSourceFieldValue("singer", "ian anderson")
    }
    "support source includes" in {
      val s = search("musicians/bands") query "jethro" sourceInclude("keyboards", "guit*")
      s should haveSourceField("keyboards")
      s should haveSourceField("guitar")
      s should not(haveSourceField("singer"))
      s should not(haveSourceField("name"))
    }
    "support source excludes" in {
      val s = search("musicians/bands") query "jethro" sourceExclude("na*", "guit*")
      s should haveSourceField("keyboards")
      s should not(haveSourceField("guitar"))
      s should haveSourceField("singer")
      s should not(haveSourceField("name"))
    }
    "support limits" in {
      search("musicians").matchAllQuery().limit(2) should haveHits(2)
      search("musicians").matchAllQuery().limit(2) should haveTotalHits(3)
    }
  }
}
