package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.RefreshPolicy
import org.scalatest.WordSpec

import scala.util.Try

class IndexMatchersTest extends WordSpec with IndexMatchers with DockerTests {

  private val indexname = "indexmatcherstest"

  Try {
    http.execute(deleteIndex(indexname)).await
  }

    Try {
    http.execute(deleteIndex("sammy")).await
  }

  http.execute {
    bulk(
      indexInto(indexname / "tubestops") fields("name" -> "south kensington", "line" -> "district"),
      indexInto(indexname / "tubestops") fields("name" -> "earls court", "line" -> "district", "zone" -> 2),
      indexInto(indexname / "tubestops") fields("name" -> "cockfosters", "line" -> "picadilly") id "3",
      indexInto(indexname / "tubestops") fields("name" -> "bank", "line" -> "northern")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  http.execute {
    createIndex("sammy")
  }.await

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
