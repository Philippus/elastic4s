package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

class CatAliasTest extends FlatSpec with Matchers with DockerTests {

  client.execute {
    indexInto("catalias/landmarks").fields("name" -> "hampton court palace").refresh(RefreshPolicy.Immediate)
  }.await

  client.execute {
    aliases(
      addAlias("ally1").on("catalias"),
      addAlias("ally2").on("catalias")
    )
  }.await

  "cats aliases" should "return all aliases" in {
    val result = client.execute {
      catAliases()
    }.await
    result.result.map(_.alias).toSet.contains("ally1") shouldBe true
    result.result.map(_.alias).toSet.contains("ally2") shouldBe true
  }
}
