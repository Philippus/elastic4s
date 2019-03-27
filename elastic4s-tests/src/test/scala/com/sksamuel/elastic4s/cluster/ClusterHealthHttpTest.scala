package com.sksamuel.elastic4s.cluster

import com.sksamuel.elastic4s.http.cluster.{ClusterHealthIndex, ClusterHealthShard}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class ClusterHealthHttpTest extends WordSpec with Matchers with DockerTests {

  private val Index1Name = "cluster-health-1"
  private val Index2Name = "cluster-health-2"
  private val Index3Name = "cluster-health-3"

  Try {
    client.execute {
      deleteIndex(Index1Name, Index2Name, Index3Name)
    }.await
  }

  client.execute {
    createIndex(Index1Name)
      .shards(1)
      .replicas(0)
      .waitForActiveShards(1)
  }.await

  client.execute {
    createIndex(Index2Name)
      .shards(1)
      .replicas(1)
      .waitForActiveShards(1)
  }.await

  client.execute {
    createIndex(Index3Name)
      .shards(5)
      .replicas(2)
      .waitForActiveShards(1)
  }.await

  "cluster health request" should {
    "return cluster health for whole cluster" in {

      val health = client
        .execute {
          clusterHealth()
        }.await.result

      health.clusterName shouldBe "docker-cluster"
      health.timeOut shouldBe false
      health.status should fullyMatch regex "(yellow)|(red)".r
      health.activeShards should be > 0
      health.activePrimaryShards should be > 0
      health.numberOfDataNodes shouldBe 1

      health.indices shouldBe null
    }
  }

  // active_shards_percent_as_number seems to be buggy and doesn't always filter correctly ;-(

  "cluster health request with indices filter" should {
    "return cluster health" in {

      val health = client
        .execute {
          clusterHealth(Index1Name, Index2Name)
        }.await.result

      health.timeOut shouldBe false
      health.clusterName shouldBe "docker-cluster"
      health.status shouldBe "yellow"
      health.numberOfNodes shouldBe 1
      health.numberOfDataNodes shouldBe 1

      health.activeShards shouldBe 2
      health.activePrimaryShards shouldBe 2
      health.relocatingShards shouldBe 0
      health.initializingShards shouldBe 0
      health.unassignedShards shouldBe 1
      health.delayedUnassignedShards shouldBe 0
      health.numberOfPendingTasks shouldBe 0
      health.numberOfInFlightFetch shouldBe 0
      health.activeShardsPercentAsNumber should be < 100.0

      health.indices shouldBe null
    }

    "return cluster health with indices level" in {

      val health = client
        .execute {
          clusterHealth(Index1Name, Index2Name) level ClusterHealthLevel.Indices
        }.await.result

      health.timeOut shouldBe false
      health.clusterName shouldBe "docker-cluster"
      health.status shouldBe "yellow"
      health.numberOfNodes shouldBe 1
      health.numberOfDataNodes shouldBe 1

      health.activeShards shouldBe 2
      health.activePrimaryShards shouldBe 2
      health.relocatingShards shouldBe 0
      health.initializingShards shouldBe 0
      health.unassignedShards shouldBe 1
      health.delayedUnassignedShards shouldBe 0
      health.numberOfPendingTasks shouldBe 0
      health.numberOfInFlightFetch shouldBe 0
      health.activeShardsPercentAsNumber should be < 100.0

      health.indices shouldBe Map(
        Index1Name -> ClusterHealthIndex("green", 1, 0, 1, 1, 0, 0, 0, null),
        Index2Name -> ClusterHealthIndex("yellow", 1, 1, 1, 1, 0, 0, 1, null)
      )
    }

    "return cluster health with shards level" in {

      val health = client
        .execute {
          clusterHealth(Index1Name, Index3Name) level ClusterHealthLevel.Shards
        }.await.result

      health.timeOut shouldBe false
      health.clusterName shouldBe "docker-cluster"
      health.status shouldBe "yellow"
      health.numberOfNodes shouldBe 1
      health.numberOfDataNodes shouldBe 1

      health.activeShards shouldBe 6
      health.activePrimaryShards shouldBe 6
      health.relocatingShards shouldBe 0
      health.initializingShards shouldBe 0
      health.unassignedShards shouldBe 10
      health.delayedUnassignedShards shouldBe 0
      health.numberOfPendingTasks shouldBe 0
      health.numberOfInFlightFetch shouldBe 0
      health.activeShardsPercentAsNumber should be < 100.0

      health.indices shouldBe Map(
        Index1Name -> ClusterHealthIndex("green", 1, 0, 1, 1, 0, 0, 0, Map(
          "0" -> ClusterHealthShard("green", primaryActive = true, 1, 0, 0, 0)
        )),
        Index3Name -> ClusterHealthIndex("yellow", 5, 2, 5, 5, 0, 0, 10, Map(
          "0" -> ClusterHealthShard("yellow", primaryActive = true, 1, 0, 0, 2),
          "1" -> ClusterHealthShard("yellow", primaryActive = true, 1, 0, 0, 2),
          "2" -> ClusterHealthShard("yellow", primaryActive = true, 1, 0, 0, 2),
          "3" -> ClusterHealthShard("yellow", primaryActive = true, 1, 0, 0, 2),
          "4" -> ClusterHealthShard("yellow", primaryActive = true, 1, 0, 0, 2)
        ))
      )
    }
  }
}
