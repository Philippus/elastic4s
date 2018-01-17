package com.sksamuel.elastic4s.reindex

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class ReindexTest extends WordSpec with Matchers with DockerTests {

  deleteIdx("reindex")
  deleteIdx("reindex2")
  deleteIdx("reindextarget")

  create("reindex")
  create("reindex2")
  create("reindextarget")

  def deleteIdx(name: String) = Try {
    http.execute {
      deleteIndex(name)
    }.await
  }

  def create(name: String) = Try {
    http.execute {
      createIndex(name)
    }.await
  }

  http.execute {
    bulk(
      indexInto("reindex" / "a").fields(Map("foo" -> "far")),
      indexInto("reindex" / "a").fields(Map("moo" -> "mar")),
      indexInto("reindex" / "a").fields(Map("moo" -> "mar")),
      indexInto("reindex2" / "a").fields(Map("goo" -> "gar"))
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "a reindex request" should {
    "copy from one index to another" in {
      http.execute {
        reindex("reindex", "reindextarget").refresh(RefreshPolicy.IMMEDIATE)
      }.await.right.get.result.created shouldBe 3

      http.execute {
        search("reindextarget")
      }.await.right.get.result.size shouldBe 3
    }
    "support size parameter" in {

      deleteIdx("reindextarget")
      create("reindextarget")

      http.execute {
        reindex("reindex", "reindextarget").size(2).refresh(RefreshPolicy.IMMEDIATE)
      }.await.right.get.result.created shouldBe 2

      http.execute {
        search("reindextarget")
      }.await.right.get.result.size shouldBe 2
    }
    "support multiple sources" in {

      deleteIdx("reindextarget")
      create("reindextarget")

      http.execute {
        reindex(Seq("reindex", "reindex2"), "reindextarget").refresh(RefreshPolicy.IMMEDIATE)
      }.await.right.get.result.created shouldBe 4

      http.execute {
        search("reindextarget")
      }.await.right.get.result.size shouldBe 4
    }
    "return failure for index not found" in {
      http.execute {
        reindex("wibble", "reindextarget").refresh(RefreshPolicy.IMMEDIATE)
      }.await.left.get.error.`type` shouldBe "index_not_found_exception"
    }
  }
}
