package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.SearchIterator
import com.sksamuel.elastic4s.testkit.{DockerTests, ElasticMatchers}
import org.scalatest.WordSpec

import scala.concurrent.duration._
import scala.util.Try

class SearchIteratorTest
  extends WordSpec
    with ElasticMatchers
    with DockerTests {

  implicit val duration: FiniteDuration = 10.seconds

  Try {
    client.execute {
      ElasticDsl.deleteIndex("searchiterator")
    }.await
  }

  client.execute {
    createIndex("searchiterator").mappings(
      mapping("pieces").fields(
        keywordField("name")
      )
    )
  }.await

  client.execute {
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
      SearchIterator.hits(client, search("searchiterator").matchAllQuery().sortBy(fieldSort("name")).size(2).scroll("1m"))
        .toList.map(_.sourceField("name")) shouldBe List("bishop", "king", "knight", "pawn", "queen", "rook")
    }
  }
}
