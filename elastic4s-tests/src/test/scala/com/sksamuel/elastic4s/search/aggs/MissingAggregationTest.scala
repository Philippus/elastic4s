package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FreeSpec, Matchers}

class MissingAggregationTest extends FreeSpec with SharedElasticSugar with Matchers with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    createIndex("missingagg") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true),
        intField("floors").stored(true)
      )
    }
  }.await

  http.execute(
    bulk(
      indexInto("missingagg/buildings") fields("name" -> "Willis Tower", "floors" -> 4),
      indexInto("missingagg/buildings") fields("name" -> "Burj Kalifa", "height" -> 2456),
      indexInto("missingagg/buildings") fields("name" -> "Tower of London", "floors" -> 7),
      indexInto("missingagg/buildings") fields("name" -> "London Bridge", "height" -> 63)
    ).refresh(RefreshPolicy.IMMEDIATE)
  ).await

  "missing aggregation" - {
    "should create a bucket for docs missing the value" in {

      val resp = http.execute {
        search("missingagg").matchAllQuery().aggs {
          missingAgg("agg1", "height").subaggs {
            sumAgg("agg2", "floors")
          }
        }
      }.await
      resp.totalHits shouldBe 4
      resp.filterAgg("agg1").docCount shouldBe 2
      resp.filterAgg("agg1").sumAgg("agg2").value shouldBe 11
    }
  }
}
