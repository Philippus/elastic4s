package com.sksamuel.elastic4s.requests.delete

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class DeleteByQueryTest extends WordSpec with Matchers with DockerTests {

  private val indexname = "charles_dickens"

  Try {
    client.execute {
      deleteIndex(indexname)
    }.await
  }

  client.execute {
    createIndex(indexname).mappings(
      mapping(indexname).fields(
        textField("name")
      )
    ).shards(1).waitForActiveShards(1)
  }.await

  "delete by query" should {
    "delete matched docs" in {
      client.execute {
        bulk(
          indexInto(indexname / indexname).fields("name" -> "mr bumbles").id("1"),
          indexInto(indexname / indexname).fields("name" -> "artful dodger").id("2"),
          indexInto(indexname / indexname).fields("name" -> "mrs bumbles").id("3"),
          indexInto(indexname / indexname).fields("name" -> "fagan").id("4")
        ).refresh(RefreshPolicy.Immediate)
      }.await

      client.execute {
        search(indexname).matchAllQuery()
      }.await.result.totalHits shouldBe 4

      client.execute {
        deleteByQuery(indexname, indexname, matchQuery("name", "bumbles")).refresh(RefreshPolicy.Immediate)
      }.await.result.deleted shouldBe 2

      client.execute {
        search(indexname).matchAllQuery()
      }.await.result.totalHits shouldBe 2
    }
    "return a Left[RequestFailure] when the delete fails" in {
      client.execute {
        deleteByQuery(",", indexname, matchQuery("name", "bumbles"))
      }.await.error.`type` shouldBe "action_request_validation_exception"
    }
  }
}
