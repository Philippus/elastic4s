package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FlatSpec, Matchers}

class MatchQueryTest
  extends FlatSpec
    with SharedElasticSugar
    with Matchers
    with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    createIndex("units")
  }.await

  http.execute {
    bulk(
      indexInto("units/base") fields("name" -> "candela", "scientist.name" -> "Jules Violle", "scientist.country" -> "France")
    ).refresh(RefreshPolicy.IMMEDIATE)
  }.await

  "a match query" should "support selecting nested properties" in {

    val resp = http.execute {
      search("units") query matchQuery("name", "candela") sourceInclude "scientist.name"
    }.await

    resp.hits.hits.head.sourceAsMap shouldBe Map("scientist.name" -> "Jules Violle")
  }
}
