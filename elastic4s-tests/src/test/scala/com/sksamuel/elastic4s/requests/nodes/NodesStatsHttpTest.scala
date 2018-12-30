package com.sksamuel.elastic4s.requests.nodes

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

class NodesStatsHttpTest extends WordSpec with Matchers with DockerTests {

  "node stats request" should {
    "return os information" in {
      val stats = client.execute {
        nodeStats()
      }.await

      stats.result.clusterName should be("docker-cluster")
      stats.result.nodes.nonEmpty shouldBe true
    }
  }
}
