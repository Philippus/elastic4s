package com.sksamuel.elastic4s.validate

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DualClientTests
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class ValidateTest extends WordSpec with Matchers with ElasticDsl with DualClientTests {

  override protected def beforeRunTests(): Unit = {

    Try {
      execute {
        deleteIndex("food")
      }.await
    }

    execute {
      createIndex("food").mappings(
        mapping("pasta").fields(
          textField("name"),
          textField("color"),
          dateField("sellbydate")
        )
      )
    }.await

    execute {
      indexInto("food/pasta") fields(
        "name" -> "maccaroni",
        "color" -> "yellow",
        "sellbydate" -> "2005-01-01"
      ) refresh RefreshPolicy.WaitFor
    }.await
  }

  "a validate query" should {
    "return valid when the query is valid for a string query" in {
      val resp = execute {
        validateIn("food/pasta") query "maccaroni"
      }.await
      resp.valid shouldBe true
    }
    "return valid when the query is valid for a dsl query" in {
      val resp = execute {
        validateIn("food/pasta") query {
          matchQuery("name", "maccaroni")
        }
      }.await
      resp.isValid shouldBe true
    }
    "return invalid when the query is nonsense" in {
      val resp = execute {
        validateIn("food/pasta") query {
          matchQuery("sellbydate", "qweqwe")
        }
      }.await
      resp.isValid shouldBe false
    }
  }
}
