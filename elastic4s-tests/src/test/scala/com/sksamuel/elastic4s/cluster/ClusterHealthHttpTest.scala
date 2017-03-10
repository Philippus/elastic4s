package com.sksamuel.elastic4s.cluster

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.cluster.ClusterStateResponse.Index
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.cluster.health.ClusterHealthStatus
import org.scalatest.{Matchers, WordSpec}

class ClusterHealthHttpTest extends WordSpec with Matchers with SharedElasticSugar with ElasticDsl {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  "cluster health request" should {
    "return healthy cluster information" in {
      http.execute {
        createIndex("mountains")
          .shards(1)
          .replicas(0)
          .waitForActiveShards(1)
      }.await

      val health = http.execute {
        clusterHealth() waitForStatus ClusterHealthStatus.GREEN
      }.await

      health.clusterName should be("classloader-node")
      health.status should be("green")
      health.activePrimaryShards should be(1)
      health.activeShards should be(1)
    }
  }
}

