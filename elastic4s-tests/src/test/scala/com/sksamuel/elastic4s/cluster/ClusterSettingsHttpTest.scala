package com.sksamuel.elastic4s.cluster

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class ClusterSettingsHttpTest extends WordSpec with Matchers with DockerTests {

  private val indexname = "clustersettingstest"

  Try {
    http.execute {
      deleteIndex(indexname)
    }.await
  }

  http.execute {
    createIndex(indexname)
      .shards(1)
      .replicas(0)
      .waitForActiveShards(1)
  }.await

  "cluster settings request" should {
    "return cluster settings updated" in {

      val settings = http.execute {
        clusterPersistentSettings(Map("indices.recovery.max_bytes_per_sec" → "50mb"))
          .transientSettings(Map("search.max_buckets" → "30000"))
      }.await.right.get.result

      settings.transient shouldBe Map("search.max_buckets" → "30000")
      settings.persistent shouldBe Map("indices.recovery.max_bytes_per_sec" → "50mb")
    }
  }
}

