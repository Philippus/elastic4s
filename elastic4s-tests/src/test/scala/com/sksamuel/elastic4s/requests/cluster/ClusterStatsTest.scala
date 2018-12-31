package com.sksamuel.elastic4s.requests.cluster

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FunSuite, Matchers}

import scala.util.Try

class ClusterStatsTest extends FunSuite with Matchers with DockerTests {

  private val indexname = "clusterstatstest"

  Try {
    client.execute {
      deleteIndex(indexname)
    }.await
  }

  client.execute {
    createIndex(indexname)
      .shards(1)
      .replicas(0)
      .waitForActiveShards(1)
  }.await

  test("cluster stats request should return cluster stats") {

    val stats = client.execute {
      clusterStats()
    }.await.result

    stats.clusterName shouldBe "docker-cluster"
    stats.clusterUUID should not be null
    stats.status shouldBe "yellow"

    stats.indices.shards.total should be > 0
    stats.indices.shards.primaries should be > 0

    stats.indices.shards.index.shards.min should be > 0
    stats.indices.shards.index.shards.max should be > 0

    stats.indices.count should be > 0
    stats.indices.docs.count should be > 0L
    stats.indices.store.sizeInBytes should be > 0L
    stats.indices.fieldData.memorySizeInBytes should be > 0L
  }
}
