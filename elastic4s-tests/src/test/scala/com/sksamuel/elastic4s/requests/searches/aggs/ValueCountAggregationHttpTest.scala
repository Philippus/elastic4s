package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class ValueCountAggregationHttpTest extends FreeSpec with Matchers with DockerTests {

  Try {
    client.execute {
      ElasticDsl.deleteIndex("valuecount")
    }.await
  }

  client.execute {
    createIndex("valuecount") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  Try {
    client.execute {
      ElasticDsl.deleteIndex("valuecount2")
    }.await
  }

  client.execute {
    createIndex("valuecount2") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("valuecount/buildings") fields("name" -> "Willis Tower", "height" -> 1244),
      indexInto("valuecount/buildings") fields("name" -> "Burj Kalifa", "height" -> 2456),
      indexInto("valuecount/buildings") fields("name" -> "Tower of London", "height" -> 169)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "cardinality agg" - {
    "should return the count of distinct values" in {
      val resp = client.execute {
        search("valuecount").matchAllQuery().aggs {
          valueCountAgg("agg1", "name")
        }
      }.await.result
      resp.totalHits shouldBe 3
      val agg = resp.aggs.valueCount("agg1")
      agg.value shouldBe 7
    }
    "should support when no documents match" in {
      val resp = client.execute {
        search("valuecount2").matchAllQuery().aggs {
          valueCountAgg("agg1", "name")
        }
      }.await.result
      resp.totalHits shouldBe 0
      val agg = resp.aggs.valueCount("agg1")
      agg.value shouldBe 0
    }
  }
}
