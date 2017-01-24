package com.sksamuel.elastic4s.validate

import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._

class ValidateTest extends WordSpec with SharedElasticSugar with Matchers {

  implicit val duration: Duration = 10.seconds

  client.execute {
    indexInto("food/pasta") fields(
      "name" -> "maccaroni",
      "color" -> "yellow"
      )
  }.await

  blockUntilCount(1, "food")

  "a validate query" should {
    "return valid when the query is valid for a string query" in {
      val resp = client.execute {
        validateIn("food/pasta") query "maccaroni"
      }.await
      resp.isValid shouldBe true
    }
    "return valid when the query is valid for a dsl query" in {
      val resp = client.execute {
        validateIn("food/pasta") query {
          bool {
            should {
              termQuery("name", "maccaroni")
            }
          }
        }
      }.await
      resp.isValid shouldBe true
    }
  }
}
