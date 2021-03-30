package com.sksamuel.elastic4s.requests.validate

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Try

class ValidateTest extends AnyWordSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("food")
    }.await
  }

  client.execute {
    createIndex("food").mapping(
      properties(
        textField("name"),
        textField("color"),
        dateField("sellbydate")
      )
    )
  }.await

  client.execute {
    indexInto("food") fields(
      "name" -> "maccaroni",
      "color" -> "yellow",
      "sellbydate" -> "2005-01-01"
    ) refresh RefreshPolicy.WaitFor
  }.await

  "a validate query" should {
    "return valid when the query is valid for a string query" in {
      val resp = client.execute {
        validateIn("food") query "maccaroni"
      }.await.result
      resp.valid shouldBe true
    }
    "return valid when the query is valid for a dsl query" in {
      val resp = client.execute {
        validateIn("food") query {
          matchQuery("name", "maccaroni")
        }
      }.await.result
      resp.isValid shouldBe true
    }
    "return invalid when the query is nonsense" in {
      val resp = client.execute {
        validateIn("food") query {
          matchQuery("sellbydate", "qweqwe")
        }
      }.await.result
      resp.isValid shouldBe false
    }
  }
}
