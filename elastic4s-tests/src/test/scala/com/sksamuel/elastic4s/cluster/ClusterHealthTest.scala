package com.sksamuel.elastic4s.cluster

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import com.sksamuel.elastic4s.testkit.{DualClient, DualElasticSugar}
import org.elasticsearch.cluster.health.ClusterHealthStatus
import org.scalatest.{Matchers, WordSpec}

class ClusterHealthTest extends WordSpec with Matchers with ElasticDsl with DualElasticSugar with DualClient {

  "cluster health request" should {
    "return healthy cluster information" in {
      execute {
        createIndex("mountains")
          .shards(1)
          .replicas(0)
          .waitForActiveShards(1)
      }.await

      val health = execute {
        clusterHealth() waitForStatus ClusterHealthStatus.GREEN
      }.await

      health.clusterName should startWith("node_")
      health.status should be("green")
      health.activePrimaryShards should be(1)
      health.activeShards should be(1)
    }
  }
}

