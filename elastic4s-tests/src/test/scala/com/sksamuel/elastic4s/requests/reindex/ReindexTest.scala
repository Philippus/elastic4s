package com.sksamuel.elastic4s.requests.reindex

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ReindexTest extends AnyWordSpec with Matchers with DockerTests {

  deleteIdx("reindex")
  deleteIdx("reindex2")
  deleteIdx("reindextarget")

  createIdx("reindex")
  createIdx("reindex2")
  createIdx("reindextarget")

  client.execute {
    bulk(
      indexInto("reindex").fields(Map("foo" -> "far")),
      indexInto("reindex").fields(Map("moo" -> "mar")),
      indexInto("reindex").fields(Map("moo" -> "mar")),
      indexInto("reindex2").fields(Map("goo" -> "gar"))
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "a reindex request" should {
    "copy from one index to another" in {
      client.execute {
        reindex("reindex", "reindextarget").refresh(RefreshPolicy.IMMEDIATE)
      }.await.result.left.get.created shouldBe 3

      client.execute {
        search("reindextarget")
      }.await.result.size shouldBe 3
    }
    "support size parameter" in {

      deleteIdx("reindextarget")
      createIdx("reindextarget")

      client.execute {
        reindex("reindex", "reindextarget").size(2).refresh(RefreshPolicy.IMMEDIATE)
      }.await.result.left.get.created shouldBe 2

      client.execute {
        search("reindextarget")
      }.await.result.size shouldBe 2
    }
    "support script parameter" in {
      deleteIdx("reindextarget")
      createIdx("reindextarget")

      client.execute {
        reindex("reindex", "reindextarget").script("ctx._source.scripted=42").refresh(RefreshPolicy.IMMEDIATE)
      }.await.result.left.get.created shouldBe 3
      client.execute {
        search("reindextarget")
      }.await.result.hits.hits.flatMap(_.sourceAsMap.get("scripted")) shouldBe Array(42, 42, 42)
    }
    "support proceed parameter" in {
      deleteIdx("reindextarget")
      createIdx("reindextarget")

      client.execute {
        reindex("reindex", "reindextarget").proceedOnConflicts(true).refresh(RefreshPolicy.IMMEDIATE)
      }.await.result.left.get.created shouldBe 3

      deleteIdx("reindextarget")
      createIdx("reindextarget")

      client.execute {
        reindex("reindex", "reindextarget").proceedOnConflicts(false).refresh(RefreshPolicy.IMMEDIATE)
      }.await.result.left.get.created shouldBe 3
    }
    "support multiple sources" in {

      deleteIdx("reindextarget")
      createIdx("reindextarget")

      client.execute {
        reindex(Seq("reindex", "reindex2"), "reindextarget").refresh(RefreshPolicy.IMMEDIATE)
      }.await.result.left.get.created shouldBe 4

      client.execute {
        search("reindextarget")
      }.await.result.size shouldBe 4
    }
    "return failure for index not found" in {
      client.execute {
        reindex("wibble", "reindextarget").refresh(RefreshPolicy.IMMEDIATE)
      }.await.error.`type` shouldBe "index_not_found_exception"
    }
    "return a task when setting wait_for_completion to false" in {
      val result = client.execute {
        reindex("reindex", "reindextarget").size(2).waitForCompletion(false)
      }.await.result.right.get
      result.nodeId should not be null
      result.taskId should not be null
    }

    "apply targetType to dest index - not to the source index" in {
      // although https://www.elastic.co/guide/en/elasticsearch/reference/7.11/docs-reindex.html#docs-reindex-api-request-body specifies
      // that type under source is ignored (see explanation of type under dest in request body), the below test fails
      // if the targetType is applied to the source index
      deleteIdx("reindextarget")
      createIdx("reindextarget")

      client.execute {
        reindex(Seq("reindex"), "reindextarget").copy(targetType = Some("test")).refresh(RefreshPolicy.IMMEDIATE)
      }.await.result.left.get.created shouldBe 3

      client.execute {
        search("reindextarget")
      }.await.result.size shouldBe 3
    }
  }
}
