package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CatAliasTest extends AnyFlatSpec with Matchers with DockerTests {

  client.execute {
    indexInto("catalias").fields("name" -> "hampton court palace").refresh(RefreshPolicy.Immediate)
  }.await

  client.execute {
    aliases(
      addAlias("ally1", "catalias"),
      addAlias("ally2", "catalias"),
      addAlias("ally22", "catalias")
    )
  }.await

  "cats aliases" should "return all aliases" in {
    val result = client.execute {
      catAliases()
    }.await
    result.result.map(_.alias).toSet.contains("ally1") shouldBe true
    result.result.map(_.alias).toSet.contains("ally2") shouldBe true
  }

  it should "return aliases matching a pattern" in {
    val result = client.execute {
      catAliases("ally2*")
    }.await
    result.result.map(_.alias).toSet.contains("ally1") shouldBe false
    result.result.map(_.alias).toSet.contains("ally2") shouldBe true
    result.result.map(_.alias).toSet.contains("ally22") shouldBe true
  }
}
