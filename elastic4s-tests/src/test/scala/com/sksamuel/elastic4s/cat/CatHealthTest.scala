package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

class CatHealthTest extends FlatSpec with Matchers with DockerTests {

  client.execute {
    bulk(
      indexInto("cathealth/landmarks").fields("name" -> "hampton court palace")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "cat health" should "return cluster health" in {
    client.execute {
      catHealth()
    }.await.result.cluster shouldBe "docker-cluster"
  }

}
