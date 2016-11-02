package com.sksamuel.elastic4s2.testkit

import com.sksamuel.elastic4s2.ElasticDsl._
import org.scalatest.WordSpec

class IndexMatchersTest extends WordSpec with IndexMatchers with ElasticSugar {

  val indexname = getClass.getSimpleName.toLowerCase

  client.execute {
    bulk(
      index into indexname / "tubestops" fields("name" -> "south kensington", "line" -> "district"),
      index into indexname / "tubestops" fields("name" -> "earls court", "line" -> "district", "zone" -> 2),
      index into indexname / "tubestops" fields("name" -> "cockfosters", "line" -> "picadilly") id 3,
      index into indexname / "tubestops" fields("name" -> "bank", "line" -> "northern")
    )
  }.await

  client.execute {
    create index "sammy"
  }.await

  blockUntilCount(4, indexname)

  "index matchers" should {
    "support index document count" in {
      indexname should haveCount(4)
      indexname should not(haveCount(11))
    }
    "support doc exists" in {
      indexname should containDoc(3)
      indexname should not(containDoc(44))
    }
    "support index exists" in {
      indexname should beCreated
      "qweqwe" should not(beCreated)
    }
    "support isEmpty" in {
      indexname should not(beEmpty)
      "sammy" should beEmpty
    }
  }
}
