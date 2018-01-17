package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.{ElasticsearchClientUri, RefreshPolicy}
import com.sksamuel.elastic4s.http.search.SearchIterator
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.{DiscoveryLocalNodeProvider, DockerTests, ElasticMatchers, ElasticSugar}
import org.scalatest.WordSpec

import scala.concurrent.duration._
import scala.util.Try

class SearchIteratorTest
  extends WordSpec
    with ElasticMatchers
    with DockerTests {

  implicit val duration: FiniteDuration = 10.seconds

  Try {
    http.execute {
      ElasticDsl.deleteIndex("searchiterator")
    }.await
  }

  http.execute {
    createIndex("searchiterator").mappings(
      mapping("pieces").fields(
        keywordField("name")
      )
    )
  }.await

  http.execute {
    bulk(
      indexInto("searchiterator/pieces").fields(
        "name" -> "queen",
        "value" -> 10,
        "count" -> 1
      ),
      indexInto("searchiterator/pieces").fields(
        "name" -> "king",
        "value" -> 0,
        "count" -> 1
      ),
      indexInto("searchiterator/pieces").fields(
        "name" -> "bishop",
        "value" -> 3,
        "count" -> 2
      ),
      indexInto("searchiterator/pieces").fields(
        "name" -> "knight",
        "value" -> 3,
        "count" -> 2
      ),
      indexInto("searchiterator/pieces").fields(
        "name" -> "rook",
        "value" -> 5,
        "count" -> 2
      ),
      indexInto("searchiterator/pieces").fields(
        "name" -> "pawn",
        "value" -> 1,
        "count" -> 8
      )
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "a search iterator" should {
    "return all documents in the search request" in {
      SearchIterator.hits(http, search("searchiterator").matchAllQuery().sortBy(fieldSort("name")).size(2).scroll("1m"))
        .toList.map(_.sourceField("name")) shouldBe List("bishop", "king", "knight", "pawn", "queen", "rook")
    }
  }
}
