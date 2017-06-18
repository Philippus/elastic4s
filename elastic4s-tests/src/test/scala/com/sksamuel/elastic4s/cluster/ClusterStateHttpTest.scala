package com.sksamuel.elastic4s.cluster

import com.sksamuel.elastic4s.http.cluster.ClusterStateResponse.Index
import com.sksamuel.elastic4s.testkit.DualClientTests
import org.scalatest.{Matchers, WordSpec}

class ClusterStateHttpTest extends WordSpec with Matchers with DualClientTests {

  "cluster state request" should {
    "return cluster state information" in {
      http.execute {
        createIndex(indexname)
          .shards(1)
          .replicas(0)
          .waitForActiveShards(1)
      }.await

      val state = http.execute {
        clusterState()
      }.await

      state.clusterName should be("localnode-cluster")

      val indexMetadata = state.metadata.flatMap(m => m.indices.headOption).map(_._2).getOrElse(Index("closed", Seq.empty))

      indexMetadata should be(Index("open", Seq.empty))
    }
  }
}

