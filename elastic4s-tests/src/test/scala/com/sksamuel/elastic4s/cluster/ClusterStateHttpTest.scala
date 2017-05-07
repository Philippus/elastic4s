package com.sksamuel.elastic4s.cluster

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.cluster.ClusterStateResponse.Index
import com.sksamuel.elastic4s.http.{HttpClient, ElasticDsl}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.scalatest.{Matchers, WordSpec}

class ClusterStateHttpTest extends WordSpec with Matchers with SharedElasticSugar with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  "cluster state request" should {
    "return cluster state information" in {
      http.execute {
        createIndex("mountains")
          .shards(1)
          .replicas(0)
          .waitForActiveShards(1)
      }.await

      val state = http.execute {
        clusterState()
      }.await

      state.cluster_name should be("classloader-node")

      val indexMetadata = state.metadata.flatMap(m => m.indices.headOption).map(_._2).getOrElse(Index("closed", Seq.empty))

      indexMetadata should be(Index("open", Seq.empty))
    }
  }
}

