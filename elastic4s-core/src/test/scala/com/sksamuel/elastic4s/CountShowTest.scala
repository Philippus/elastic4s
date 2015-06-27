package com.sksamuel.elastic4s

import org.scalatest.{Matchers, WordSpec}

class CountShowTest extends WordSpec with Matchers with ElasticSugar {

  import ElasticDsl._

  "Search" should {
    "have a show typeclass implementation" in {
      val request = {
        count from "gameofthrones" / "characters" query {
          bool {
            should {
              termQuery("name", "snow")
            }.must {
              matchQuery("location", "the wall")
            }
          }
        } routing "routing" preference "prefs" minScore 1.4
      }
      request.show shouldBe """{
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
