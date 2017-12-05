package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.jackson.ElasticJackson
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class SearchHttpTest extends WordSpec with DiscoveryLocalNodeProvider with ElasticDsl with Matchers {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  Try {
    http.execute {
      ElasticDsl.deleteIndex("chess")
    }.await
  }

  http.execute {
    createIndex("chess").mappings(
      mapping("pieces").fields(
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
      )
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "a search query" should {
    "find an indexed document that matches a term query" in {
      http.execute {
        search("chess") query termQuery("name", "pawn")
      }.await.right.get.result.totalHits shouldBe 1
    }
    "find an indexed document in the given type only" in {
      http.execute {
        search("chess" / "pieces") query matchQuery("name", "queen")
      }.await.right.get.result.totalHits shouldBe 1
    }
    "support match all query" in {
      http.execute {
        search("chess") query matchAllQuery()
      }.await.right.get.result.totalHits shouldBe 6
    }
    "support sorting in a single type" in {
      http.execute {
        search("chess" / "pieces") query matchAllQuery() sortBy fieldSort("name")
      }.await.right.get.result.hits.hits.map(_.sourceField("name")) shouldBe Array("bishop", "king", "knight", "pawn", "queen", "rook")
    }
    "support explain" in {
      http.execute {
        search("chess").explain(true).matchAllQuery().limit(2)
      }.await.right.get.result.hits.hits.head.explanation.isDefined shouldBe true
    }
    "support limits" in {
      http.execute {
        search("chess").matchAllQuery().limit(2)
      }.await.right.get.result.size shouldBe 2
    }
    "support unmarshalling through a HitReader" in {
      implicit val reader = ElasticJackson.Implicits.JacksonJsonHitReader[Piece]
      http.execute {
        search("chess") query matchAllQuery() sortBy fieldSort("name") size 3
      }.await.right.get.result.to[Piece] shouldBe Vector(Piece("bishop", 3, 2), Piece("king", 0, 1), Piece("knight", 3, 2))
    }
    "support source includes" in {
      http.execute {
        search("chess") query matchAllQuery() sourceInclude "count"
      }.await.right.get.result.hits.hits.map(_.sourceAsString).toSet shouldBe Set("{\"count\":1}", "{\"count\":2}", "{\"count\":8}")
    }
    "support source excludes" in {
      http.execute {
        search("chess") query matchAllQuery() sourceExclude "count"
      }.await.right.get.result.hits.hits.map(_.sourceAsString).toSet shouldBe Set("{\"name\":\"pawn\",\"value\":1}", "{\"name\":\"knight\",\"value\":3}", "{\"name\":\"king\",\"value\":0}", "{\"name\":\"rook\",\"value\":5}", "{\"name\":\"queen\",\"value\":10}", "{\"name\":\"bishop\",\"value\":3}")
    }
    "support constantScoreQuery" should {
      "work with termQuery" in {
        http.execute {
          search("chess") query {
            constantScoreQuery {
              termQuery("name", "pawn")
            }
          }
        }.await.right.get.result.totalHits shouldBe 1
      }
      "work with termsQuery" in {
        http.execute {
          search("chess") query {
            constantScoreQuery {
              termsQuery("name", List("pawn", "king"))
            }
          }
        }.await.right.get.result.totalHits shouldBe 2
      }
      "support boost and queryName" in {
        val resp = http.execute {
          search("chess") query {
            constantScoreQuery {
              termQuery("name", "pawn")
            } boost 14.5 queryName "namey"
          }
        }.await.right.get.result
        resp.totalHits shouldBe 1
        resp.maxScore shouldBe 14.5
      }
      "not throw npe on empty hits for safeTo" in {
        http.execute {
          search("chess") query {
            matchQuery("name", "werwerewrewrewr")
          }
        }.await.right.get.result.safeTo[Piece].size shouldBe 0
      }
      "support search hits without null on no hits" in {
        http.execute {
          search("chess") query {
            matchQuery("name", "werwerewrewrewr")
          }
        }.await.right.get.result.hits.hits.isEmpty shouldBe true
      }
      "return Left[RequestFailure] when searching an unknown index" in {
        http.execute {
          search("qweqweqwe") query {
            matchQuery("name", "werwerewrewrewr")
          }
        }.await.left.get.error.`type` shouldBe "index_not_found_exception"
      }
      "return Left[RequestFailure] when the search is invalid" in {
        http.execute {
          search("qweqweqwe").rawQuery("""{"unknown" : "unk" } """)
        }.await.left.get.error.`type` shouldBe "parsing_exception"
      }
      "not throw npe for max score on empty hits" in {
        http.execute {
          search("chess") query {
            matchQuery("name", "werwerewrewrewr")
          }
        }.await.right.get.result.maxScore shouldBe 0
      }
    }
  }
}

case class Piece(name: String, value: Int, count: Int)
