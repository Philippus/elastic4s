package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FreeSpec, Matchers}

class AvgAggregationHttpTest extends FreeSpec with SharedElasticSugar with Matchers with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    createIndex("avgagg") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  http.execute(
    bulk(
      indexInto("avgagg/buildings") fields("name" -> "Willis Tower", "height" -> 1244),
      indexInto("avgagg/buildings") fields("name" -> "Burj Kalifa", "height" -> 2456),
      indexInto("avgagg/buildings") fields("name" -> "Tower of London", "height" -> 169)
    ).refresh(RefreshPolicy.IMMEDIATE)
  ).await

  "avg agg" - {
    "should return the avg for the context" in {

      val resp = http.execute {
        search("avgagg").matchAllQuery().aggs {
          avgAgg("agg1", "height")
        }
      }.await
      resp.totalHits shouldBe 3
      val agg = resp.maxAgg("agg1")
      agg.value > 1289 shouldBe true
      agg.value > 1290 shouldBe false
    }
  }
}
