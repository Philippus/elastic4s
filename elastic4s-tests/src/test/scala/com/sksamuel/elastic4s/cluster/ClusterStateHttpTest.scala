package com.sksamuel.elastic4s.cluster

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.http.cluster.ClusterStateResponse.Index
import com.sksamuel.elastic4s.testkit.{DiscoveryLocalNodeProvider, DualClientTests}
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class ClusterStateHttpTest extends WordSpec with Matchers with DiscoveryLocalNodeProvider with ElasticDsl {

  private val indexname = "clusterstatetest"

  Try {
    http.execute {
      deleteIndex(indexname)
    }.await
  }

  http.execute {
    createIndex(indexname)
      .shards(1)
      .replicas(0)
      .waitForActiveShards(1)
  }.await

  "cluster state request" should {
    "return cluster state information" ignore {

      val state = http.execute {
        clusterState()
      }.await.right.get.result

      state.clusterName should be("localnode-cluster")

      val indexMetadata = state.metadata.flatMap(m => m.indices.headOption).map(_._2).getOrElse(Index("closed", Seq.empty))

      indexMetadata should be(Index("open", Seq.empty))
    }
  }
}

