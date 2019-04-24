package com.sksamuel.elastic4s.requests.cat

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class CatShardsTest extends FlatSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("catshards1")
    }.await
    client.execute {
      deleteIndex("catshards2")
    }.await
  }

  client.execute {
    bulk(
      indexInto("catshards1").fields("name" -> "hampton court palace"),
      indexInto("catshards1").fields("name" -> "stonehenge"),
      indexInto("catshards1").fields("name" -> "kensington palace"),
      indexInto("catshards2").fields("name" -> "blenheim palace"),
      indexInto("catshards2").fields("name" -> "london eye"),
      indexInto("catshards2").fields("name" -> "tower of london")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "cats shards" should "return all shards" ignore {
    val result = client.execute {
      catShards()
    }.await
    result.result.map(_.state).toSet shouldBe Set("STARTED", "UNASSIGNED")
    result.result.map(_.index).contains("catshards1") shouldBe true
    result.result.map(_.index).contains("catshards2") shouldBe true
  }
}
