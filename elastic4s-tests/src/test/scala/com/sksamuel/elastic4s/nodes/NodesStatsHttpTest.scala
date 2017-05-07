package com.sksamuel.elastic4s.nodes

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{HttpClient, ElasticDsl}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.scalatest.{Matchers, WordSpec}

class NodesStatsHttpTest extends WordSpec with Matchers with SharedElasticSugar with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  "node stats request" should {
    "return os information" in {
      val stats = http.execute {
        nodeStats()
      }.await

      stats.nodes.size should be(1)
      stats.clusterName should be("classloader-node")
    }
  }
}
