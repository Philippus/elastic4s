package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class ExistsTest extends WordSpec with Matchers with DockerTests {

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
    indexInto("exists/flowers").withId("a").fields("name" -> "Narcissus")
  }.await

  "an exists request" should {
    "return true for an existing doc" in {
      client.execute {
        exists("a", "exists", "flowers")
      }.await.result shouldBe true
    }
    "return false for non existing doc" in {
      client.execute {
        exists("b", "exists", "flowers")
      }.await.result shouldBe false
    }
  }
}
