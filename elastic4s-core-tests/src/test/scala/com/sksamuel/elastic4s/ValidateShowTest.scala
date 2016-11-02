package com.sksamuel.elastic4s

import org.scalatest.{Matchers, WordSpec}
import com.sksamuel.elastic4s.testkit.ElasticSugar

class ValidateShowTest extends WordSpec with Matchers with ElasticSugar {

  import ElasticDsl._

  "Search" should {
    "have a show typeclass implementation" in {
      val request = {
        validate in "gameofthrones" / "characters" query {
          bool {
            should {
              termQuery("name", "snow")
            }.must {
              matchQuery("location", "the wall")
            }
          }
        } explain true
      }
      request.show shouldBe """{
                              |  "bool" : {
                              |    "must" : {
                              |      "match" : {
                              |        "location" : {
                              |          "query" : "the wall",
                              |          "type" : "boolean"
                              |        }
                              |      }
                              |    },
                              |    "should" : {
                              |      "term" : {
                              |        "name" : "snow"
                              |      }
                              |    }
                              |  }
                              |}""".stripMargin
    }
  }
}
