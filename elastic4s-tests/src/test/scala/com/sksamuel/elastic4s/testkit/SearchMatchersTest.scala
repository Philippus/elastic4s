package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.ElasticApi
import org.scalatest.WordSpec

import scala.util.Try

class SearchMatchersTest extends WordSpec with SearchMatchers with ElasticApi with DiscoveryLocalNodeProvider {

  val indexname = "searchmatchers"
  val tubestops = "tubestops"

  import com.sksamuel.elastic4s.ElasticDsl._

  Try {
    client.execute {
      deleteIndex(indexname)
    }.await
  }

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
    ).immediateRefresh()
  }.await

  "search matchers" should {
    "support haveHit" in {
      (search(indexname) query "picadilly") should containId(3)
    }
    "support haveHits" in {
      (search(indexname) query "*") should haveHits(4)
      (search(indexname) query "bank") should haveHits(1)
    }
    "support haveNoHits" in {
      (search(indexname) query "aldgate") should haveNoHits
    }
  }
}
