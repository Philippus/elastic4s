package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.{ElasticMatchers, ElasticSugar}
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.WordSpec

class RangeQueryHttpTest
  extends WordSpec
    with ElasticSugar
    with ElasticMatchers
    with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    createIndex("rangequeryhttptest").mappings(
      mapping("pieces").fields(
        textField("name").fielddata(true)
      ),
      mapping("openings").fields(
        textField("name").fielddata(true)
      )
    )
  }.await

  http.execute {
    bulk(
      indexInto("rangequeryhttptest/pieces").fields(
        "name" -> "queen",
        "value" -> 10,
        "count" -> 1
      ),
      indexInto("rangequeryhttptest/pieces").fields(
        "name" -> "king",
        "value" -> 0,
        "count" -> 1
      ),
      indexInto("rangequeryhttptest/pieces").fields(
        "name" -> "bishop",
        "value" -> 3,
        "count" -> 2
      ),
      indexInto("rangequeryhttptest/pieces").fields(
        "name" -> "knight",
        "value" -> 3,
        "count" -> 2
      ),
      indexInto("rangequeryhttptest/pieces").fields(
        "name" -> "rook",
        "value" -> 5,
        "count" -> 2
      ),
      indexInto("rangequeryhttptest/pieces").fields(
        "name" -> "pawn",
        "value" -> 1,
        "count" -> 8
      )
    ).refresh(RefreshPolicy.IMMEDIATE)
  }.await

  "a range query" should {
    "support using gte" in {
      val resp = http.execute {
        search("rangequeryhttptest" / "pieces") query {
          // bishop, rook, castle, queen
          rangeQuery("value").gte("3")
        }
      }.await
      resp.totalHits shouldBe 4
    }
    "support lte" in {
      val resp = http.execute {
        search("rangequeryhttptest" / "pieces") query {
          // pawns, king, bisho
          rangeQuery("value").lte("3")
        }
      }.await
      resp.totalHits shouldBe 4
    }
    "support using both lte & gte" in {
      val resp = http.execute {
        search("rangequeryhttptest" / "pieces") query {
          rangeQuery("value").gte("5").lte("7")
        }
      }.await
      resp.totalHits shouldBe 1
    }
    "support integers" in {
      val resp = http.execute {
        search("rangequeryhttptest" / "pieces") query {
          rangeQuery("value").gte(5).lte(7)
        }
      }.await
      resp.totalHits shouldBe 1
    }
    "support doubles" in {
      val resp = http.execute {
        search("rangequeryhttptest" / "pieces") query {
          rangeQuery("value").gte(5.0).lte(7.0)
        }
      }.await
      resp.totalHits shouldBe 1
    }
    "support boost" in {
      val resp = http.execute {
        search("rangequeryhttptest" / "pieces") query {
          rangeQuery("value").lte("3").boost(14.5)
        }
      }.await
      resp.maxScore shouldBe 14.5
    }
  }
}
