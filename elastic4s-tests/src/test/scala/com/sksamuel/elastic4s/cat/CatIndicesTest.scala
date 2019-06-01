package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.testkit.DockerTests
import com.sksamuel.elastic4s.requests.common.{HealthStatus, RefreshPolicy}
import org.scalatest.{FlatSpec, Matchers}

class CatIndicesTest extends FlatSpec with Matchers with DockerTests {

  client.execute {
    bulk(
      indexInto("catindex1").fields("name" -> "hampton court palace"),
      indexInto("catindex2").fields("name" -> "hampton court palace"),
      indexInto("catindex3").fields("name" -> "hampton court palace"),
      indexInto("catindex33").fields("name" -> "hampton court palace")
    ).refresh(RefreshPolicy.Immediate)
  }.await


  "catIndices" should "return all indexes" in {
    val indexes = client.execute {
      catIndices()
    }.await.result.map(_.index).toSet
    indexes.contains("catindex1") shouldBe true
    indexes.contains("catindex2") shouldBe true
    indexes.contains("catindex3") shouldBe true
  }

  it should "return all indexes matching a pattern" in {
    val indexes = client.execute {
      catIndices("catindex3*")
    }.await.result.map(_.index).toSet

    indexes.contains("catindex1") shouldBe false
    indexes.contains("catindex2") shouldBe false
    indexes.contains("catindex3") shouldBe true
    indexes.contains("catindex33") shouldBe true
  }

  it should "use health param" in {
    client.execute {
      catIndices(HealthStatus.Red)
    }.await.result.isEmpty shouldBe true
  }

  it should "include pri.store.size" in {
    client.execute {
      catIndices()
    }.await.result.head.priStoreSize > 0 shouldBe true
  }

}
