package com.sksamuel.elastic4s.requests.admin

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ShrinkIndexTest extends AnyWordSpec with Matchers with DockerTests with Eventually {

  val reindexTarget = "reindextarget"
  val reindex       = "reindex"

  deleteIdx(reindex)
  deleteIdx(reindexTarget)

  createIdx(reindex, shards = 2)

  client.execute {
    bulk(
      indexInto(reindex).fields(Map("foo" -> "far")),
      indexInto(reindex).fields(Map("moo" -> "mar")),
      indexInto(reindex).fields(Map("moo" -> "mar"))
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "a shrink index request" should {
    "copy from one index to another with new shards number" in {
      client.execute {
        updateIndexLevelSettings(reindex).settings(Map("index.blocks.write" -> true.toString))
      }.await.result

      client.execute {
        recoverIndex(reindex)
      }.await.result(reindex).shards.size shouldBe 2

      client.execute {
        shrinkIndex(reindex, reindexTarget).shards(1)
      }.await.result

      eventually {
        val resp = client.execute(recoverIndex(reindexTarget)).await.result
        (resp(reindexTarget).shards.forall(_.get("stage").contains("DONE")) && resp.size == 1) shouldBe true
      }

      client.execute {
        search(reindexTarget)
      }.await.result.size shouldBe 3
    }
  }
}
