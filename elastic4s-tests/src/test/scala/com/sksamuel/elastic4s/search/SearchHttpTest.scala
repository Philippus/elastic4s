package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.{ElasticMatchers, ElasticSugar}
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.WordSpec

class SearchHttpTest
  extends WordSpec
    with ElasticSugar
    with ElasticMatchers
    with ElasticDsl {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    bulk(
      indexInto("chess/pieces").fields(
        "name" -> "queen",
        "value" -> 10,
        "count" -> 1
      ),
      indexInto("chess/pieces").fields(
        "name" -> "pawn",
        "value" -> 1,
        "count" -> 8
      ),
      indexInto("chess/openings").fields(
        "name" -> "queen gambit",
        "rank" -> 0.2
      ),
      indexInto("chess/pieces").fields(
        "name" -> "modern defence",
        "rank" -> -0.1
      )
    ).refresh(RefreshPolicy.IMMEDIATE)
  }.await

  "a search query" should {
    "find an indexed document that matches a term query" in {
      http.execute {
        search("chess" / "pieces") query termQuery("name", "pawn")
      }.await.totalHits shouldBe 1
    }
    "find an indexed document in the given type only" in {
      http.execute {
        search("chess") query matchQuery("name", "queen")
      }.await.totalHits shouldBe 2
      http.execute {
        search("chess" / "pieces") query matchQuery("name", "queen")
      }.await.totalHits shouldBe 1
    }
    "support limits" in {
      http.execute {
        search("chess").matchAll().limit(2)
      }.await.size shouldBe 2
      http.execute {
        search("chess").matchAll()
      }.await.size shouldBe 4
    }
  }
}
