package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FreeSpec, Matchers}

class MinMaxAggregationHttpTest extends FreeSpec with SharedElasticSugar with Matchers with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    createIndex("minmaxagg") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  http.execute(
    bulk(
      indexInto("minmaxagg/buildings") fields("name" -> "Willis Tower", "height" -> 1244),
      indexInto("minmaxagg/buildings") fields("name" -> "Burj Kalifa", "height" -> 2456),
      indexInto("minmaxagg/buildings") fields("name" -> "Tower of London", "height" -> 169)
    ).refresh(RefreshPolicy.IMMEDIATE)
  ).await

  "max agg" - {
    "should return the max for the context" in {

      val resp = http.execute {
        search("minmaxagg").matchAllQuery().aggs {
          maxAgg("agg1", "height")
        }
      }.await
      resp.totalHits shouldBe 3
      val agg = resp.maxAgg("agg1")
      agg.value shouldBe 2456
    }
  }

  "min agg" - {
    "should return the max for the context" in {

      val resp = http.execute {
        search("minmaxagg").matchAllQuery().aggs {
          minAgg("agg1", "height")
        }
      }.await
      resp.totalHits shouldBe 3
      val agg = resp.minAgg("agg1")
      agg.value shouldBe 169
    }
  }
}
