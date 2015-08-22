package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.ElasticDsl._
import org.scalatest.WordSpec

class SearchMatchersTest extends WordSpec with SearchMatchers with ElasticSugar {

  val indexname = getClass.getSimpleName.toLowerCase

  client.execute {
    bulk(
      index into indexname / "tubestops" fields("name" -> "south kensington", "line" -> "district"),
      index into indexname / "tubestops" fields("name" -> "earls court", "line" -> "district", "zone" -> 2),
      index into indexname / "tubestops" fields("name" -> "cockfosters", "line" -> "picadilly") id 3,
      index into indexname / "tubestops" fields("name" -> "bank", "line" -> "northern")
    )
  }.await

  blockUntilCount(4, indexname)

  "search matchers" should {
    "support haveHit" in {
      (search in indexname query "cockfosters") should containDoc(3)
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
