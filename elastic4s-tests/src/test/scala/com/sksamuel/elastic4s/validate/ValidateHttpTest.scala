package com.sksamuel.elastic4s.validate

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{Matchers, WordSpec}

class ValidateHttpTest extends WordSpec with SharedElasticSugar with Matchers with ElasticDsl {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    createIndex("validate").mappings(
      mapping("pasta").fields(
        textField("name"),
        textField("color"),
        dateField("sellbydate")
      )
    )
  }

  http.execute {
    indexInto("validate/pasta") fields(
      "name" -> "maccaroni",
      "color" -> "yellow",
      "sellbydate" -> "2005-01-01"
    ) refresh RefreshPolicy.WAIT_UNTIL
  }.await

  "a validate query" should {
    "return valid when the query is valid for a string query" in {
      val resp = http.execute {
        validateIn("validate/pasta") query "maccaroni"
      }.await
      resp.valid shouldBe true
    }
    "return valid when the query is valid for a dsl query" in {
      val resp = http.execute {
        validateIn("validate/pasta") query {
          matchQuery("name", "maccaroni")
        }
      }.await
      resp.isValid shouldBe true
    }
    "return invalid when the query is nonsense" in {
      val resp = http.execute {
        validateIn("validate/pasta") query {
          matchQuery("sellbydate", "qweqwe")
        }
      }.await
      resp.isValid shouldBe false
    }
  }
}
