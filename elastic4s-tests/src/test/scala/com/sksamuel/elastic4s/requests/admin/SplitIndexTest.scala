package com.sksamuel.elastic4s.requests.admin

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class SplitIndexTest extends AnyWordSpec with Matchers with DockerTests {

  val reindexTarget = "reindextarget"
  val reindex       = "reindex"

  deleteIdx(reindex)
  deleteIdx(reindexTarget)

  createIdx(reindex)

  client.execute {
    bulk(
      indexInto(reindex).fields(Map("foo" -> "far")),
      indexInto(reindex).fields(Map("moo" -> "mar")),
      indexInto(reindex).fields(Map("moo" -> "mar"))
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "a split index request" should {
    "copy from one index to another with new shards number" in {

      client.execute {
        updateIndexLevelSettings(reindex).settings(Map("index.blocks.write" -> true.toString))
      }.await.result

      client.execute {
        recoverIndex(reindex)
      }.await.result(reindex).shards.size shouldBe 1

      client.execute {
        splitIndex(reindex, reindexTarget).shards(2).settings(Map("index.number_of_routing_shards" -> 2.toString))
      }.await.result

      Stream.continually {
        Thread.sleep(100)
        val resp = client.execute(recoverIndex(reindexTarget)).await.result
        resp(reindexTarget).shards.forall(_.get("stage").contains("DONE")) && resp.size == 2
      }.takeWhile(!_)

      client.execute {
        search(reindexTarget)
      }.await.result.size shouldBe 3
    }
  }
}
