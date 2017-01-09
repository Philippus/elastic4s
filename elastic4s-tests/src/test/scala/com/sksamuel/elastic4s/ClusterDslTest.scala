package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.admin.cluster.health.{ClusterHealthAction, ClusterHealthRequestBuilder}
import org.elasticsearch.action.support.ActiveShardCount
import org.elasticsearch.common.Priority
import org.elasticsearch.common.unit.TimeValue
import org.scalatest.{FlatSpec, Matchers}

class ClusterDslTest extends FlatSpec with Matchers {

  "a cluster health request" should "convert empty indices to _all" in {
    val req = clusterHealth()
    val builder = new ClusterHealthRequestBuilder(ProxyClients.client, ClusterHealthAction.INSTANCE)
    req.build(builder)
    builder.request.indices() shouldBe Array("_all")
  }

  it should "accept a list of indices" in {
    val req = clusterHealth("index1", "index2")
    val builder = new ClusterHealthRequestBuilder(ProxyClients.client, ClusterHealthAction.INSTANCE)
    req.build(builder)
    builder.request.indices() shouldBe Array("index1", "index2")
  }

  it should "allow waiting for active shards" in {
    val req = clusterHealth("index1", "index2").waitForActiveShards(3)
    val builder = new ClusterHealthRequestBuilder(ProxyClients.client, ClusterHealthAction.INSTANCE)
    req.build(builder)
    builder.request.waitForActiveShards() shouldBe ActiveShardCount.from(3)
  }

  it should "allow waiting for events" in {
    val req = clusterHealth("index1", "index2").waitForEvents(Priority.IMMEDIATE)
    val builder = new ClusterHealthRequestBuilder(ProxyClients.client, ClusterHealthAction.INSTANCE)
    req.build(builder)
    builder.request.waitForEvents() shouldBe Priority.IMMEDIATE
  }

  it should "allow a timeout" in {
    val req = clusterHealth().timeout("1s")
    val builder = new ClusterHealthRequestBuilder(ProxyClients.client, ClusterHealthAction.INSTANCE)
    req.build(builder)
    builder.request.timeout() shouldBe TimeValue.timeValueSeconds(1)
  }
}
