package com.sksamuel.elastic4s.nodes

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

class NodesInfoHttpTest extends WordSpec with Matchers with DiscoveryLocalNodeProvider with ElasticDsl {

  "node info request" should {
    "return node information" in {
      val nodes = http.execute {
        nodeInfo()
      }.await.right.get.result

      nodes.clusterName should be("localnode-cluster")
      nodes.nodes.values.toSeq.head.os.availableProcessors > 0 shouldBe true
    }
  }
}
