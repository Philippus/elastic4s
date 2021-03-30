package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class GlobalAggregationHttpTest extends AnyFreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("globalagg")
    }.await
  }

  client.execute {
    createIndex("globalagg") mappings {
      mapping(keywordField("name"))
    }
  }.await

  client.execute(
    bulk(
      indexInto("globalagg") fields("name" -> "cyan"),
      indexInto("globalagg") fields("name" -> "magenta"),
      indexInto("globalagg") fields("name" -> "yellow"),
      indexInto("globalagg") fields("name" -> "black"),
      indexInto("globalagg") fields("name" -> "black")
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "global agg" - {
    "should be not influenced by the search query" in {

      val resp = client.execute {
        search("globalagg").termQuery("name", "black").aggs {
          globalAggregation("global")
        }
      }.await.result

      resp.totalHits shouldBe 2
      resp.aggs.global("global").docCount shouldBe 5
    }

    "should allow to use subaggregations" in {

      val resp = client.execute {
        search("globalagg").termQuery("name", "yellow").aggs {
          globalAggregation("global").subaggs {
            filterAgg("blackAgg", termQuery("name", "black"))
          }
        }
      }.await.result

      resp.totalHits shouldBe 1
      resp.aggs.global("global").docCount shouldBe 5
      resp.aggs.global("global").filter("blackAgg").docCount shouldBe 2
    }

  }
}
