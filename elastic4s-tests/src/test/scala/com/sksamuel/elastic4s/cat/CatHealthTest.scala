package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.{DockerTests, RefreshPolicy}
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{FlatSpec, Matchers}

class CatHealthTest extends FlatSpec with Matchers with DockerTests {

  http.execute {
    bulk(
      indexInto("cathealth/landmarks").fields("name" -> "hampton court palace")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "cat health" should "return cluster health" in {
    http.execute {
      catHealth()
    }.await.right.get.result.cluster shouldBe "localnode-cluster"
  }

}
