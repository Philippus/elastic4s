package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FreeSpec, Matchers}

class FilterAggregationTest extends FreeSpec with SharedElasticSugar with Matchers with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    createIndex("filteragg") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  http.execute(
    bulk(
      indexInto("filteragg/buildings") fields("name" -> "Willis Tower", "height" -> 1244),
      indexInto("filteragg/buildings") fields("name" -> "Burj Kalifa", "height" -> 2456),
      indexInto("filteragg/buildings") fields("name" -> "Tower of London", "height" -> 169),
      indexInto("filteragg/buildings") fields("name" -> "London Bridge", "height" -> 63)
    ).refresh(RefreshPolicy.IMMEDIATE)
  ).await

  "filter ag" - {
    "should create a bucket matching the query" in {

      val resp = http.execute {
        search("filteragg").matchAllQuery().aggs {
          filterAgg("agg1", matchQuery("name", "london")).subaggs {
            sumAgg("agg2", "height")
          }
        }
      }.await
      resp.totalHits shouldBe 4
      resp.filterAgg("agg1").docCount shouldBe 2
      resp.filterAgg("agg1").sumAgg("agg2").value shouldBe 232
    }
  }
}
