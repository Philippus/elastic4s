package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.testkit.{ElasticMatchers, ElasticSugar}
import org.scalatest.WordSpec
import org.scalatest.concurrent.Eventually

class SearchTest
  extends WordSpec
    with ElasticSugar
    with Eventually
    with ElasticMatchers {

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
    "return source" in {
      searchIn("musicians" / "bands").query("jethro") should haveSourceFieldValue("singer", "ian anderson")
    }
    "support source includes" in {
      val s = search in "musicians/bands" query "jethro" sourceInclude("keyboards", "guit*")
      s should haveSourceField("keyboards")
      s should haveSourceField("guitar")
      s should not(haveSourceField("singer"))
      s should not(haveSourceField("name"))
    }
    "support source excludes" in {
      val s = search in "musicians/bands" query "jethro" sourceExclude("na*", "guit*")
      s should haveSourceField("keyboards")
      s should not(haveSourceField("guitar"))
      s should haveSourceField("singer")
      s should not(haveSourceField("name"))
    }
    "support limits" in {
      searchIn("musicians").matchAll().limit(2) should haveHits(2)
      searchIn("musicians").matchAll().limit(2) should haveTotalHits(3)
    }
  }
}
