package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class SumAggregationHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("sumagg")
    }.await
  }

  client.execute {
    createIndex("sumagg") mappings {
      mapping("actors") fields(
        textField("name").fielddata(true),
        intField("age").stored(true)
      )
    }
  }.await.result.acknowledged shouldBe true


  Try {
    client.execute {
      deleteIndex("sumagg2")
    }.await
  }

  client.execute {
    createIndex("sumagg2") mappings {
      mapping("actors") fields(
        textField("name").fielddata(true),
        intField("age").stored(true)
      )
    }
  }.await.result.acknowledged shouldBe true

  client.execute(
    bulk(
      indexInto("sumagg/actors") fields("name" -> "clint eastwood", "age" -> "52"),
      indexInto("sumagg/actors") fields("name" -> "eli wallach", "age" -> "72"),
      indexInto("sumagg/actors") fields("name" -> "lee van cleef", "age" -> "62"),
      indexInto("sumagg/actors") fields("name" -> "nicholas cage"),
      indexInto("sumagg/actors") fields("name" -> "sean connery", "age" -> "32"),
      indexInto("sumagg/actors") fields("name" -> "kevin costner", "age" -> "42")
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "sum aggregation" - {
    "should group by field" in {

      val resp = client.execute {
        search("sumagg").matchAllQuery().aggs {
          sumAgg("agg1", "age")
        }
      }.await.result
      resp.totalHits shouldBe 6

      val agg = resp.aggs.sum("agg1")
      agg.value shouldBe 260.0
    }
    "should support missing" in {
      val resp = client.execute {
        search("sumagg").matchAllQuery().aggs {
          sumAgg("agg1", "age").missing("100")
        }
      }.await.result
      resp.totalHits shouldBe 6

      val agg = resp.aggs.sum("agg1")
      agg.value shouldBe 360
    }
    "should support when no documents match" in {
      val resp = client.execute {
        search("sumagg2").matchAllQuery().aggs {
          sumAgg("agg1", "age").missing("100")
        }
      }.await.result
      resp.totalHits shouldBe 0

      val agg = resp.aggs.sum("agg1")
      agg.value shouldBe 0
    }
  }
}
