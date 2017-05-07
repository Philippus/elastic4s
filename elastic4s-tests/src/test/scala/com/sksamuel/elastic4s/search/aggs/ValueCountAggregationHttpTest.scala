package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FreeSpec, Matchers}

class ValueCountAggregationHttpTest extends FreeSpec with SharedElasticSugar with Matchers with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    createIndex("valuecount") mappings {
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
    ).refresh(RefreshPolicy.IMMEDIATE)
  ).await

  "cardinality agg" - {
    "should return the count of distinct values" in {

      val resp = http.execute {
        search("valuecount").matchAllQuery().aggs {
          valueCountAgg("agg1", "name")
        }
      }.await
      resp.totalHits shouldBe 3
      val agg = resp.maxAgg("agg1")
      agg.value shouldBe 7
    }
  }
}
