package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.{ElasticDsl, HitReader}
import com.sksamuel.elastic4s.jackson.ElasticJackson
import com.sksamuel.elastic4s.requests.admin.IndicesOptionsRequest
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.queries.matches.MultiMatchQueryBuilderType.CROSS_FIELDS
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class SearchTest extends WordSpec with DockerTests with Matchers {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  Try {
    client.execute {
      ElasticDsl.deleteIndex("chess")
    }.await
  }

  client.execute {
    createIndex("chess").mappings(
      mapping("pieces").fields(
        textField("name").fielddata(true)
      )
    )
  }.await

  client.execute {
    bulk(
      indexInto("chess/pieces").fields(
        "name" -> "queen",
        "value" -> 10,
        "count" -> 1
      ).routing("wibble"),
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
        "aka" -> "horse",
        "value" -> 3,
        "count" -> 2
      ),
      indexInto("chess/pieces").fields(
        "name" -> "rook",
        "aka" -> "castle",
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
      client.execute {
        search("chess") query termQuery("name", "pawn")
      }.await.result.totalHits shouldBe 1
    }
    "find an indexed document in the given type only" in {
      client.execute {
        search("chess") query matchQuery("name", "queen")
      }.await.result.totalHits shouldBe 1
    }
    "support match all query" in {
      client.execute {
        search("chess") query matchAllQuery()
      }.await.result.totalHits shouldBe 6
    }
    "support sorting in a single type" in {
      client.execute {
        search("chess") query matchAllQuery() sortBy fieldSort("name")
      }.await.result.hits.hits.map(_.sourceField("name")) shouldBe Array("bishop", "king", "knight", "pawn", "queen", "rook")
    }
    "support explain" in {
      client.execute {
        search("chess").explain(true).matchAllQuery().limit(2)
      }.await.result.hits.hits.head.explanation.isDefined shouldBe true
    }
    "support limits" in {
      client.execute {
        search("chess").matchAllQuery().limit(2)
      }.await.result.size shouldBe 2
    }
    "support unmarshalling through a HitReader" in {
      implicit val reader: HitReader[Piece] = ElasticJackson.Implicits.JacksonJsonHitReader[Piece]
      client.execute {
        search("chess") query matchAllQuery() sortBy fieldSort("name") size 3
      }.await.result.to[Piece] shouldBe Vector(Piece("bishop", 3, 2), Piece("king", 0, 1), Piece("knight", 3, 2))
    }
    "support source includes" in {
      client.execute {
        search("chess") query matchAllQuery() sourceInclude "count"
      }.await.result.hits.hits.map(_.sourceAsString).toSet shouldBe Set("{\"count\":1}", "{\"count\":2}", "{\"count\":8}")
    }
    "support source excludes" in {
      client.execute {
        search("chess") query matchAllQuery() sourceExclude "count"
      }.await.result.hits.hits.map(_.sourceAsString).toSet shouldBe Set("{\"name\":\"pawn\",\"value\":1}", "{\"aka\":\"horse\",\"name\":\"knight\",\"value\":3}", "{\"name\":\"king\",\"value\":0}", "{\"aka\":\"castle\",\"name\":\"rook\",\"value\":5}", "{\"name\":\"queen\",\"value\":10}", "{\"name\":\"bishop\",\"value\":3}")
    }
    "support constantScoreQuery" should {
      "work with termQuery" in {
        client.execute {
          search("chess") query {
            constantScoreQuery {
              termQuery("name", "pawn")
            }
          }
        }.await.result.totalHits shouldBe 1
      }
      "work with termsQuery" in {
        client.execute {
          search("chess") query {
            constantScoreQuery {
              termsQuery("name", List("pawn", "king"))
            }
          }
        }.await.result.totalHits shouldBe 2
      }
      "support boost and queryName" in {
        val resp = client.execute {
          search("chess") query {
            constantScoreQuery {
              termQuery("name", "pawn")
            } boost 14.5 queryName "namey"
          }
        }.await.result
        resp.totalHits shouldBe 1
        resp.maxScore shouldBe 14.5
      }
    }
    "not throw npe on empty hits for safeTo" in {
      client.execute {
        search("chess") query {
          matchQuery("name", "werwerewrewrewr")
        }
      }.await.result.safeTo[Piece].size shouldBe 0
    }
    "support search hits without null on no hits" in {
      client.execute {
        search("chess") query {
          matchQuery("name", "werwerewrewrewr")
        }
      }.await.result.hits.hits.isEmpty shouldBe true
    }
    "return Left[RequestFailure] when searching an unknown index" in {
      client.execute {
        search("qweqweqwe") query {
          matchQuery("name", "werwerewrewrewr")
        }
      }.await.error.`type` shouldBe "index_not_found_exception"
    }
    "return Left[RequestFailure] when the search is invalid" in {
      client.execute {
        search("qweqweqwe").rawQuery("""{"unknown" : "unk" } """)
      }.await.error.`type` shouldBe "parsing_exception"
    }
    "not throw npe for max score on empty hits" in {
      client.execute {
        search("chess") query {
          matchQuery("name", "werwerewrewrewr")
        }
      }.await.result.maxScore shouldBe 0
    }
    "include _routing in response" in {
      val resp = client.execute {
        search("chess").termQuery("name", "queen").limit(1).routing("wibble")
      }.await
      resp.result.hits.hits.forall(_.routing.contains("wibble")) shouldBe true
    }
    "include matched_queries in response" in {
      val resp = client.execute {
        search("chess")
          .query(boolQuery().filter(termQuery("name", "queen").queryName("wibble")))
          .limit(1)
      }.await
      resp.result.hits.hits.forall(_.matchedQueries.contains(Set("wibble"))) shouldBe true
    }
    "support multi-field match types in a query string query" in {
      val resp = client.execute {
        search("chess")
          .query(
            queryStringQuery("knight horse")
              .field("name")
              .field("aka")
              .defaultOperator("AND")
              .matchType(CROSS_FIELDS)
          )
      }.await
      resp.result.totalHits shouldBe 1
    }
    "ignore unavailable should ignore unknown indexes" in {
      client.execute {
        search("chess", "qweqwewqe")
          .query(
            queryStringQuery("knight horse")
              .field("name")
              .field("aka")
              .defaultOperator("AND")
              .matchType(CROSS_FIELDS)
          ).indicesOptions(IndicesOptionsRequest(ignoreUnavailable = true))
      }.await.result.totalHits shouldBe 1
    }
  }
}

case class Piece(name: String, value: Int, count: Int)
