package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.ActiveShardCount
import org.elasticsearch.common.Priority
import org.elasticsearch.common.unit.TimeValue
import org.scalatest.{FlatSpec, Matchers}

class ClusterExecutablesTest extends FlatSpec with Matchers with SharedElasticSugar {

  "a cluster health request" should "convert empty indices to _all" in {
    val builder = ClusterHealthDefinitionExecutable.buildHealthRequest(node.client, clusterHealth())
    builder.request.indices() shouldBe Array("_all")
  }

  it should "accept a list of indices" in {
    val builder = ClusterHealthDefinitionExecutable.buildHealthRequest(node.client, clusterHealth("index1", "index2"))
    builder.request.indices() shouldBe Array("index1", "index2")
  }

  it should "allow waiting for active shards" in {
    val builder = ClusterHealthDefinitionExecutable.buildHealthRequest(node.client, clusterHealth("index1", "index2").waitForActiveShards(3))
    builder.request.waitForActiveShards() shouldBe ActiveShardCount.from(3)
  }

  it should "allow waiting for events" in {
    val builder = ClusterHealthDefinitionExecutable.buildHealthRequest(node.client, clusterHealth("index1", "index2").waitForEvents(Priority.IMMEDIATE))
    builder.request.waitForEvents() shouldBe Priority.IMMEDIATE
  }

  it should "allow a timeout" in {
    val builder = ClusterHealthDefinitionExecutable.buildHealthRequest(node.client, clusterHealth().timeout("1s"))
    builder.request.timeout() shouldBe TimeValue.timeValueSeconds(1)
  }
}
