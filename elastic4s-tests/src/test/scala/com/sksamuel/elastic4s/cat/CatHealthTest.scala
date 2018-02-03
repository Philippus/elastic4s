package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
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
    }.await.result.cluster shouldBe "docker-cluster"
  }

}
