package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.{DiscoveryLocalNodeProvider, DockerTests}
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class SumAggregationHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    http.execute {
      deleteIndex("sumagg")
    }.await
  }

  http.execute {
    createIndex("sumagg") mappings {
      mapping("actors") fields(
        textField("name").fielddata(true),
        intField("age").stored(true)
      )
    }
  }.await.result.acknowledged shouldBe true


  Try {
    http.execute {
      deleteIndex("sumagg2")
    }.await
  }

  http.execute {
    createIndex("sumagg2") mappings {
      mapping("actors") fields(
        textField("name").fielddata(true),
        intField("age").stored(true)
      )
    }
  }.await.result.acknowledged shouldBe true

  http.execute(
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

      val resp = http.execute {
        search("sumagg").matchAllQuery().aggs {
          sumAgg("agg1", "age")
        }
      }.await.result
      resp.totalHits shouldBe 6

      val agg = resp.aggs.sum("agg1")
      agg.value shouldBe 260.0
    }
    "should support missing" in {
      val resp = http.execute {
        search("sumagg").matchAllQuery().aggs {
          sumAgg("agg1", "age").missing("100")
        }
      }.await.result
      resp.totalHits shouldBe 6

      val agg = resp.aggs.sum("agg1")
      agg.value shouldBe 360
    }
    "should support when no documents match" in {
      val resp = http.execute {
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
