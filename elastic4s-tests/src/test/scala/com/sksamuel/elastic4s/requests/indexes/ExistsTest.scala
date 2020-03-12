package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Try

class ExistsTest extends AnyWordSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("exists")
    }.await
  }

  client.execute {
    createIndex("exists").mappings {
      mapping("flowers") fields textField("name")
    }
  }.await

  client.execute {
    indexInto("exists").withId("a").fields("name" -> "Narcissus")
  }.await

  "an exists request" should {
    "return true for an existing doc" in {
      client.execute {
        exists("a", "exists")
      }.await.result shouldBe true
    }
    "return false for non existing doc" in {
      client.execute {
        exists("b", "exists")
      }.await.result shouldBe false
    }
  }
}
