package com.sksamuel.elastic4s.cluster

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class NodeUsageTest extends AnyFlatSpec with Matchers with DockerTests {

  client.execute {
    bulk(
      indexInto("nodeusage").fields("name" -> "hampton court palace"),
      indexInto("nodeusage").fields("name" -> "kensington palace"),
      indexInto("nodeusage").fields("name" -> "tower of london")
    ).refresh(RefreshPolicy.Immediate)
  }.await


  "node usage" should "return all nodes" in {
    val resp = client.execute {
      nodeUsage()
    }.await.result

    resp.clusterName should not be null
    resp.nodeCounts.total shouldBe 1
    resp.nodeCounts.successful shouldBe 1
    resp.nodeCounts.failed shouldBe 0

    resp.nodes.values.head.since > 0 shouldBe true
    resp.nodes.values.head.timestamp > 0 shouldBe true
  }
}
