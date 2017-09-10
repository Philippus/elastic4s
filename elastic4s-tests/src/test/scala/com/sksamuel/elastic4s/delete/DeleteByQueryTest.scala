package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class DeleteByQueryTest extends WordSpec with Matchers with ElasticDsl with DiscoveryLocalNodeProvider {

  private val indexname = "charles_dickens"

  Try {
    http.execute {
      deleteIndex(indexname)
    }.await
  }

  http.execute {
    createIndex(indexname).mappings(
      mapping(indexname).fields(
        textField("name")
      )
    ).shards(1).waitForActiveShards(1)
  }.await

  "delete by query" should {
    "delete matched docs" in {
      http.execute {
        bulk(
          indexInto(indexname).fields("name" -> "mr bumbles").id(1),
          indexInto(indexname).fields("name" -> "artful dodger").id(2),
          indexInto(indexname).fields("name" -> "mrs bumbles").id(3),
          indexInto(indexname).fields("name" -> "fagan").id(4)
        ).refresh(RefreshPolicy.Immediate)
      }.await

      http.execute {
        search(indexname).matchAllQuery()
      }.await.right.get.totalHits shouldBe 4

      http.execute {
        deleteByQuery(indexname, matchQuery("name", "bumbles")).refresh(RefreshPolicy.Immediate)
      }.await.right.get.deleted shouldBe 2

      http.execute {
        search(indexname).matchAllQuery()
      }.await.right.get.totalHits shouldBe 2
    }
    "return a Left[RequestFailure] when the delete fails" in {
      http.execute {
        deleteByQuery(",", matchQuery("name", "bumbles"))
      }.await.left.get.error.`type` shouldBe "action_request_validation_exception"
    }
  }
}
