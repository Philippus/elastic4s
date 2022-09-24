package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.handlers.index.Stats
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.Inspectors
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class IndexStatsRequestTest
  extends AnyFlatSpec
    with Matchers
    with DockerTests
    with Inspectors {

  client.execute {
    bulk(
      indexInto("indexstats1").fields("name" -> "hampton court palace"),
      indexInto("indexstats2").fields("name" -> "tower of london"),
      indexInto("indexstats3").fields("name" -> "fountains abbey")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "index stats" should "return all indexes" in {
    val stats = client.execute {
      indexStats()
    }.await.result

    def testStats(stats: Seq[Stats]): Unit = {
      stats.map(_.docs.count).sum > 0 shouldBe true
      stats.map(_.store.sizeInBytes).sum > 0 shouldBe true
      stats.map(_.translog.operations).sum > 0 shouldBe true
      stats.map(_.segments.count).sum > 0 shouldBe true
      stats.map(_.segments.memoryInBytes).sum >= 0 shouldBe true
      stats.map(_.segments.normsMemoryInBytes).sum >= 0 shouldBe true
      stats.map(_.segments.storedFieldsMemoryInBytes).sum >= 0 shouldBe true
    }

    testStats(Seq(stats.all.primaries, stats.all.total))
    testStats(stats.indices.values.map(_.primaries).toList ++ stats.indices.values.map(_.total))
  }

}
