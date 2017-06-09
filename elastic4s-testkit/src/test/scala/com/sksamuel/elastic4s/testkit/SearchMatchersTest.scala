package com.sksamuel.elastic4s.testkit

import org.scalatest.WordSpec

class SearchMatchersTest extends WordSpec with SearchMatchers with SharedElasticSugar {

  val indexname = "searchmatchers"
  val tubestops = "tubestops"

  client.execute {
    createIndex(indexname).mappings(
      mapping(tubestops)
    )
  }

  client.execute {
    bulk(
      indexInto(indexname / "tubestops").fields("name" -> "south kensington", "line" -> "district"),
      indexInto(indexname / "tubestops").fields("name" -> "earls court", "line" -> "district", "zone" -> 2),
      indexInto(indexname / "tubestops").fields("name" -> "cockfosters", "line" -> "picadilly").id(3),
      indexInto(indexname / "tubestops").fields("name" -> "bank", "line" -> "northern")
    )
  }.await

  blockUntilCount(4, indexname)

  "search matchers" should {
    "support haveHit" in {
      (search in indexname query "picadilly") should containId(3)
    }
    "support haveHits" in {
      (search in indexname query "*") should haveHits(4)
      (search in indexname query "bank") should haveHits(1)
    }
    "support haveNoHits" in {
      (search in indexname query "aldgate") should haveNoHits
    }
  }
}
