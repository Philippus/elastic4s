package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.{ElasticsearchClientUri, RefreshPolicy}
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class MatchQueryTest
  extends FlatSpec
    with DiscoveryLocalNodeProvider
    with Matchers
    with ElasticDsl {

  Try {
    http.execute {
      deleteIndex("units")
    }.await
  }

  http.execute {
    createIndex("units")
  }.await

  http.execute {
    bulk(
      indexInto("units/base") fields("name" -> "candela", "scientist.name" -> "Jules Violle", "scientist.country" -> "France")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "a match query" should "support selecting nested properties" in {

    val resp = http.execute {
      search("units") query matchQuery("name", "candela") sourceInclude "scientist.name"
    }.await.get

    resp.hits.hits.head.sourceAsMap shouldBe Map("scientist.name" -> "Jules Violle")
  }
}
