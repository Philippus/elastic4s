package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.testkit.ElasticSugar
import com.sksamuel.elastic4s.{ElasticDsl, searches}
import org.scalatest.{Matchers, WordSpec}

class SearchShowTest extends WordSpec with Matchers with ElasticSugar {

  "Search" should {
    "have a show typeclass implementation" in {
      val request = {
        search in "gameofthrones" / "characters" query {
          bool {
            should {
              termQuery("name", "snow")
            }.must {
              matchQuery("location", "the wall")
            }
          }
        }
      }
      request.show shouldBe
        """{
                       |  "query" : {
                       |    "bool" : {
                       |      "must" : {
                       |        "match" : {
                       |          "location" : {
                       |            "query" : "the wall",
                       |            "type" : "boolean"
                       |          }
                       |        }
                       |      },
                       |      "should" : {
                       |        "term" : {
                       |          "name" : "snow"
                       |        }
                       |      }
                       |    }
                       |  }
                       |}""".stripMargin
    }
  }
}
