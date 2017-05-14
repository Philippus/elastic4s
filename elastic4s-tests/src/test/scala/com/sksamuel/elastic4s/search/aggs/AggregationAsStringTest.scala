package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FunSuite, Matchers}

class AggregationAsStringTest extends FunSuite with SharedElasticSugar with Matchers with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    createIndex("aggstring") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  http.execute(
    bulk(
      indexInto("aggstring/buildings") fields("name" -> "Willis Tower", "height" -> 1244),
      indexInto("aggstring/buildings") fields("name" -> "Burj Kalifa", "height" -> 2456),
      indexInto("aggstring/buildings") fields("name" -> "Tower of London", "height" -> 169)
    ).refresh(RefreshPolicy.IMMEDIATE)
  ).await

  test("agg as string should return aggregation json") {
    http.execute {
      search("aggstring").matchAllQuery().aggs(
        maxAgg("agg1", "height"),
        sumAgg("agg2", "height"),
        termsAgg("agg3", "name")
      )
    }.await.aggregationsAsString shouldBe
      """{"agg2":{"value":3869.0},"agg1":{"value":2456.0},"agg3":{"doc_count_error_upper_bound":0,"sum_other_doc_count":0,"buckets":[{"key":"tower","doc_count":2},{"key":"burj","doc_count":1},{"key":"kalifa","doc_count":1},{"key":"london","doc_count":1},{"key":"of","doc_count":1},{"key":"willis","doc_count":1}]}}"""
  }
}
