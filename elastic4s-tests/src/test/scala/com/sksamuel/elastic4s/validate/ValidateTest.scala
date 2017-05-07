package com.sksamuel.elastic4s.validate

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import com.sksamuel.elastic4s.testkit.{DualClient, DualElasticSugar}
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{Matchers, WordSpec}

class ValidateTest extends WordSpec with Matchers with ElasticDsl with DualElasticSugar with DualClient {

  override protected def beforeRunTests() = {
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
      ) refresh RefreshPolicy.WAIT_UNTIL
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
