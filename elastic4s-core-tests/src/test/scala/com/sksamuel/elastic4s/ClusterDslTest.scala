package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.admin.cluster.health.{ClusterHealthAction, ClusterHealthRequestBuilder}
import org.elasticsearch.common.unit.TimeValue
import org.scalatest.{FlatSpec, Matchers}

class ClusterDslTest extends FlatSpec with Matchers {

  "a cluster health request" should "accept empty indices" in {
    val req = clusterHealth()
    val builder = new ClusterHealthRequestBuilder(ProxyClients.client, ClusterHealthAction.INSTANCE)
    req.build(builder)
    builder.request.indices() shouldBe Array.empty
  }

  it should "accept a list of indices" in {
    val req = clusterHealth("index1", "index2")
    val builder = new ClusterHealthRequestBuilder(ProxyClients.client, ClusterHealthAction.INSTANCE)
    req.build(builder)
    builder.request.indices() shouldBe Array("index1", "index2")
  }

  it should "allow a timeout" in {
    val req = clusterHealth().timeout("1s")
    val builder = new ClusterHealthRequestBuilder(ProxyClients.client, ClusterHealthAction.INSTANCE)
    req.build(builder)
    builder.request.timeout() shouldBe TimeValue.timeValueSeconds(1)
  }
}
