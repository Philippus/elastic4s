package com.sksamuel.elastic4s.validate

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.{DiscoveryLocalNodeProvider, DockerTests}
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class ValidateTest extends WordSpec with Matchers with DockerTests {

  Try {
    http.execute {
      deleteIndex("food")
    }.await
  }

  http.execute {
    createIndex("food").mappings(
      mapping("pasta").fields(
        textField("name"),
        textField("color"),
        dateField("sellbydate")
      )
    )
  }.await

  http.execute {
    indexInto("food/pasta") fields(
      "name" -> "maccaroni",
      "color" -> "yellow",
      "sellbydate" -> "2005-01-01"
    ) refresh RefreshPolicy.WaitFor
  }.await

  "a validate query" should {
    "return valid when the query is valid for a string query" in {
      val resp = http.execute {
        validateIn("food/pasta") query "maccaroni"
      }.await.right.get.result
      resp.valid shouldBe true
    }
    "return valid when the query is valid for a dsl query" in {
      val resp = http.execute {
        validateIn("food/pasta") query {
          matchQuery("name", "maccaroni")
        }
      }.await.right.get.result
      resp.isValid shouldBe true
    }
    "return invalid when the query is nonsense" in {
      val resp = http.execute {
        validateIn("food/pasta") query {
          matchQuery("sellbydate", "qweqwe")
        }
      }.await.right.get.result
      resp.isValid shouldBe false
    }
  }
}
