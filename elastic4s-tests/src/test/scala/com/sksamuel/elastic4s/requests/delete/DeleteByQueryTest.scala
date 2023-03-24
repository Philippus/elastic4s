package com.sksamuel.elastic4s.requests.delete

import com.sksamuel.elastic4s.fields.TextField
import com.sksamuel.elastic4s.requests.common.{RefreshPolicy, Slice}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Try

class DeleteByQueryTest extends AnyWordSpec with Matchers with DockerTests {

  private val indexname = "charles_dickens"

  Try {
    client.execute {
      deleteIndex(indexname)
    }.await
  }

  client.execute {
    createIndex(indexname).mapping(
      properties(
        TextField("name")
      )
    ).shards(1).waitForActiveShards(1)
  }.await

  "delete by query" should {
    "delete matched docs" in {
      client.execute {
        bulk(
          indexInto(indexname).fields("name" -> "mr bumbles").id("1"),
          indexInto(indexname).fields("name" -> "artful dodger").id("2"),
          indexInto(indexname).fields("name" -> "mrs bumbles").id("3"),
          indexInto(indexname).fields("name" -> "fagan").id("4")
        ).refresh(RefreshPolicy.Immediate)
      }.await

      client.execute {
        search(indexname).matchAllQuery()
      }.await.result.totalHits shouldBe 4

      client.execute {
        deleteByQuery(indexname, matchQuery("name", "bumbles")).refresh(RefreshPolicy.Immediate)
      }.await.result.left.get.deleted shouldBe 2

      client.execute {
        search(indexname).matchAllQuery()
      }.await.result.totalHits shouldBe 2
    }

    "respect size parameter" in {

      client.execute {
        bulk(
          indexInto(indexname).fields("name" -> "mrs havisham").id("5"),
          indexInto(indexname).fields("name" -> "peggotty").id("6")
        ).refresh(RefreshPolicy.Immediate)
      }.await

      client.execute {
        search(indexname).matchAllQuery()
      }.await.result.totalHits shouldBe 4

      client.execute {
        deleteByQuery(indexname, matchAllQuery()).refresh(RefreshPolicy.Immediate).maxDocs(3)
      }.await.result.left.get.deleted shouldBe 3

      client.execute {
        search(indexname).matchAllQuery()
      }.await.result.totalHits shouldBe 1
    }

    "delete with slices" in {
      client.execute {
        bulk(
          indexInto(indexname).fields("name" -> "barkis").id("7"),
          indexInto(indexname).fields("name" -> "belle").id("8")
        ).refresh(RefreshPolicy.Immediate)
      }.await

      client.execute {
        search(indexname).query(idsQuery("7","8"))
      }.await.result.totalHits shouldBe 2

      val firstSlice = client.execute {
        deleteByQuery(indexname, idsQuery("7","8")).slice(Slice("0", 2)).refresh(RefreshPolicy.Immediate)
      }.await.result.left.get.deleted

      val secondSlice = client.execute {
        deleteByQuery(indexname, idsQuery("7", "8")).slice(Slice("1", 2)).refresh(RefreshPolicy.Immediate)
      }.await.result.left.get.deleted

      firstSlice + secondSlice shouldBe 2

      client.execute {
        search(indexname).query(idsQuery("7","8"))
      }.await.result.totalHits shouldBe 0
    }

    "return a Left[RequestFailure] when the delete fails" in {
      client.execute {
        deleteByQuery(",", matchQuery("name", "bumbles"))
      }.await.error.`type` shouldBe "action_request_validation_exception"
    }

    "return a task when setting wait_for_completion to false" in {
      val result = client.execute {
        deleteByQuery(indexname, matchQuery("name",  "michael douglas")).waitForCompletion(false)
      }.await.result.right.get
      result.nodeId should not be null
      result.taskId should not be null
    }
  }
}
