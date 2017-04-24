package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{Matchers, WordSpec}

class RangeQueryTcpTest extends WordSpec with ElasticSugar with Matchers {

  client.execute {
    createIndex("rangequerytcptest").mappings(
      mapping("pieces").fields(
        textField("name").fielddata(true)
      ),
      mapping("openings").fields(
        textField("name").fielddata(true)
      )
    )
  }.await

  client.execute {
    bulk(
      indexInto("rangequerytcptest/pieces").fields(
        "name" -> "queen",
        "value" -> 10,
        "count" -> 1
      ),
      indexInto("rangequerytcptest/pieces").fields(
        "name" -> "king",
        "value" -> 0,
        "count" -> 1
      ),
      indexInto("rangequerytcptest/pieces").fields(
        "name" -> "bishop",
        "value" -> 3,
        "count" -> 2
      ),
      indexInto("rangequerytcptest/pieces").fields(
        "name" -> "knight",
        "value" -> 3,
        "count" -> 2
      ),
      indexInto("rangequerytcptest/pieces").fields(
        "name" -> "rook",
        "value" -> 5,
        "count" -> 2
      ),
      indexInto("rangequerytcptest/pieces").fields(
        "name" -> "pawn",
        "value" -> 1,
        "count" -> 8
      )
    ).refresh(RefreshPolicy.IMMEDIATE)
  }.await

  "a range query" should {
    "support using gte" in {
      val resp = client.execute {
        search("rangequerytcptest" / "pieces") query {
          // bishop, rook, castle, queen
          rangeQuery("value").gte("3")
        }
      }.await
      resp.totalHits shouldBe 4
    }
    "support lte" in {
      val resp = client.execute {
        search("rangequerytcptest" / "pieces") query {
          // pawns, king, bishop, rook
          rangeQuery("value").lte("3")
        }
      }.await
      resp.totalHits shouldBe 4
    }
    "support using both lte & gte" in {
      val resp = client.execute {
        search("rangequerytcptest" / "pieces") query {
          rangeQuery("value").gte("5").lte("7")
        }
      }.await
      resp.totalHits shouldBe 1
    }
    "support integers" in {
      val resp = client.execute {
        search("rangequerytcptest" / "pieces") query {
          rangeQuery("value").gte(5).lte(7)
        }
      }.await
      resp.totalHits shouldBe 1
    }
    "support doubles" in {
      val resp = client.execute {
        search("rangequerytcptest" / "pieces") query {
          rangeQuery("value").gte(5.0).lte(7.0)
        }
      }.await
      resp.totalHits shouldBe 1
    }
    "support boost" in {
      val resp = client.execute {
        search("rangequerytcptest" / "pieces") query {
          rangeQuery("value").lte("3").boost(14.5)
        }
      }.await
      resp.maxScore shouldBe 14.5
    }
  }
}
