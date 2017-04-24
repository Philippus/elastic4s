package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.jackson.ElasticJackson
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
    createIndex("chess").mappings(
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
      indexInto("chess/pieces").fields(
        "name" -> "queen",
        "value" -> 10,
        "count" -> 1
      ),
      indexInto("chess/pieces").fields(
        "name" -> "king",
        "value" -> 0,
        "count" -> 1
      ),
      indexInto("chess/pieces").fields(
        "name" -> "bishop",
        "value" -> 3,
        "count" -> 2
      ),
      indexInto("chess/pieces").fields(
        "name" -> "knight",
        "value" -> 3,
        "count" -> 2
      ),
      indexInto("chess/pieces").fields(
        "name" -> "rook",
        "value" -> 5,
        "count" -> 2
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
      indexInto("chess/openings").fields(
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
    "support match all query" in {
      http.execute {
        search("chess") query matchAllQuery()
      }.await.totalHits shouldBe 8
    }
    "support sorting in a single type" in {
      http.execute {
        search("chess" / "pieces") query matchAllQuery() sortBy fieldSort("name")
      }.await.hits.hits.map(_.sourceField("name")) shouldBe Array("bishop", "king", "knight", "pawn", "queen", "rook")
      http.execute {
        search("chess" / "openings") query matchAllQuery() sortBy fieldSort("name")
      }.await.hits.hits.map(_.sourceField("name")) shouldBe Array("modern defence", "queen gambit")
    }
    "support limits" in {
      http.execute {
        search("chess").matchAllQuery().limit(2)
      }.await.size shouldBe 2
      http.execute {
        search("chess").matchAllQuery()
      }.await.size shouldBe 8
    }
    "support unmarshalling through a HitReader" in {
      implicit val reader = ElasticJackson.Implicits.JacksonJsonHitReader[Piece]
      http.execute {
        search("chess" / "pieces") query matchAllQuery() sortBy fieldSort("name") size 3
      }.await.to[Piece] shouldBe Vector(Piece("bishop", 3, 2), Piece("king", 0, 1), Piece("knight", 3, 2))
    }
    "support source includes" in {
      http.execute {
        search("chess" / "pieces") query matchAllQuery() sourceInclude "count"
      }.await.hits.hits.map(_.sourceAsString).toSet shouldBe Set("{\"count\":1}", "{\"count\":2}", "{\"count\":8}")
    }
    "support source excludes" in {
      http.execute {
        search("chess" / "pieces") query matchAllQuery() sourceExclude "count"
      }.await.hits.hits.map(_.sourceAsString).toSet shouldBe Set("{\"name\":\"pawn\",\"value\":1}", "{\"name\":\"knight\",\"value\":3}", "{\"name\":\"king\",\"value\":0}", "{\"name\":\"rook\",\"value\":5}", "{\"name\":\"queen\",\"value\":10}", "{\"name\":\"bishop\",\"value\":3}")
    }
    "support constantScoreQuery" should {
      "work with termQuery" in {
        http.execute {
          search("chess" / "pieces") query {
            constantScoreQuery {
              termQuery("name", "pawn")
            }
          }
        }.await.totalHits shouldBe 1
      }
      "work with termsQuery" in {
        http.execute {
          search("chess" / "pieces") query {
            constantScoreQuery {
              termsQuery("name", List("pawn", "king"))
            }
          }
        }.await.totalHits shouldBe 2
      }
      "support boost and queryName" in {
        val resp = http.execute {
          search("chess" / "pieces") query {
            constantScoreQuery {
              termQuery("name", "pawn")
            } boost 14.5 queryName "namey"
          }
        }.await
        resp.totalHits shouldBe 1
        resp.maxScore shouldBe 14.5
      }
      "not throw npe on empty hits" in {
        http.execute {
          search("chess" / "pieces") query {
            matchQuery("name", "werwerewrewrewr")
          }
        }.await.safeTo[Piece].size shouldBe 0
      }
      "not throw npe for max score on empty hits" in {
        http.execute {
          search("chess" / "pieces") query {
            matchQuery("name", "werwerewrewrewr")
          }
        }.await.maxScore shouldBe 0
      }
    }
  }
}

case class Piece(name: String, value: Int, count: Int)
