package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class ValueCountAggregationHttpTest extends FreeSpec with DiscoveryLocalNodeProvider with Matchers with ElasticDsl {

  Try {
    http.execute {
      ElasticDsl.deleteIndex("valuecount")
    }.await
  }

  http.execute {
    createIndex("valuecount") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  Try {
    http.execute {
      ElasticDsl.deleteIndex("valuecount2")
    }.await
  }

  http.execute {
    createIndex("valuecount2") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  http.execute(
    bulk(
      indexInto("valuecount/buildings") fields("name" -> "Willis Tower", "height" -> 1244),
      indexInto("valuecount/buildings") fields("name" -> "Burj Kalifa", "height" -> 2456),
      indexInto("valuecount/buildings") fields("name" -> "Tower of London", "height" -> 169)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "cardinality agg" - {
    "should return the count of distinct values" in {
      val resp = http.execute {
        search("valuecount").matchAllQuery().aggs {
          valueCountAgg("agg1", "name")
        }
      }.await.right.get
      resp.totalHits shouldBe 3
      val agg = resp.aggs.valueCount("agg1")
      agg.value shouldBe 7
    }
    "should support when no documents match" in {
      val resp = http.execute {
        search("valuecount2").matchAllQuery().aggs {
          valueCountAgg("agg1", "name")
        }
      }.await.right.get
      resp.totalHits shouldBe 0
      val agg = resp.aggs.valueCount("agg1")
      agg.value shouldBe 0
    }
  }
}
