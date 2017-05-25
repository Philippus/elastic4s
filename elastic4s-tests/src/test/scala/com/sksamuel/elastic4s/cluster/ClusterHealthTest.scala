package com.sksamuel.elastic4s.cluster

import com.sksamuel.elastic4s.HealthStatus
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DualClientTests
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class ClusterHealthTest extends WordSpec with Matchers with ElasticDsl with DualClientTests {

  override protected def beforeRunTests(): Unit = {

    Try {
      execute {
        deleteIndex("clusterhealth")
      }.await
    }

    execute {
      createIndex("clusterhealth")
        .shards(1)
        .replicas(0)
        .waitForActiveShards(1)
    }.await
  }

  "cluster health request" should {
    "return healthy cluster information" in {

      val health = execute {
        clusterHealth() waitForStatus HealthStatus.Green
      }.await

      health.clusterName shouldBe "classloader-node"
      health.status should be("green")
      health.activePrimaryShards should be(1)
      health.activeShards should be(1)
    }
  }
}

