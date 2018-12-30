package com.sksamuel.elastic4s.requests.delete

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class DeleteByIdTest extends WordSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("lecarre")
    }.await
  }

  client.execute {
    createIndex("lecarre").mappings(
      mapping("characters").fields(
        textField("name")
      )
    ).shards(1).waitForActiveShards(1)
  }.await

  "delete by id request" should {
    "delete matched docs" in {

      client.execute {
        indexInto("lecarre" / "characters").fields("name" -> "jonathon pine").id("2").refresh(RefreshPolicy.Immediate)
      }.await

      client.execute {
        indexInto("lecarre" / "characters").fields("name" -> "george smiley").id("4").refresh(RefreshPolicy.Immediate)
      }.await

      client.execute {
        search("lecarre" / "characters").matchAllQuery()
      }.await.result.totalHits shouldBe 2

      client.execute {
        delete("2").from("lecarre" / "characters").refresh(RefreshPolicy.Immediate)
      }.await

      client.execute {
        search("lecarre" / "characters").matchAllQuery()
      }.await.result.totalHits shouldBe 1

      client.execute {
        delete("4").from("lecarre" / "characters").refresh(RefreshPolicy.Immediate)
      }.await

      client.execute {
        search("lecarre" / "characters").matchAllQuery()
      }.await.result.totalHits shouldBe 0
    }
  }
}
