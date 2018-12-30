package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class CatCountTest extends FlatSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("catcount1")
    }.await
    client.execute {
      deleteIndex("catcount2")
    }.await
  }

  client.execute {
    bulk(
      indexInto("catcount1/landmarks").fields("name" -> "hampton court palace"),
      indexInto("catcount1/landmarks").fields("name" -> "tower of london"),
      indexInto("catcount2/landmarks").fields("name" -> "stonehenge")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "cats count" should "return count for all cluster" in {
    client.execute {
      catCount()
    }.await.result.count >= 3 shouldBe true
  }

  it should "support counting for a single index" in {
    client.execute {
      catCount("catcount1")
    }.await.result.count shouldBe 2
  }

  it should "support counting for multiple indices" in {
    client.execute {
      catCount("catcount1", "catcount2")
    }.await.result.count shouldBe 3
  }

}
