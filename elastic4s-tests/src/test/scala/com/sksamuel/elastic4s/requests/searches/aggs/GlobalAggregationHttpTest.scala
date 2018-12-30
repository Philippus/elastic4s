package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class GlobalAggregationHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("globalagg")
    }.await
  }

  client.execute {
    createIndex("globalagg") mappings {
      mapping("colors") fields keywordField("name")
    }
  }.await

  client.execute(
    bulk(
      indexInto("globalagg/colors") fields("name" -> "cyan"),
      indexInto("globalagg/colors") fields("name" -> "magenta"),
      indexInto("globalagg/colors") fields("name" -> "yellow"),
      indexInto("globalagg/colors") fields("name" -> "black"),
      indexInto("globalagg/colors") fields("name" -> "black")
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
