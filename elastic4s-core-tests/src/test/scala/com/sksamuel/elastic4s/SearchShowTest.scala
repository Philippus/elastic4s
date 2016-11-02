package com.sksamuel.elastic4s

import org.scalatest.{Matchers, WordSpec}
import com.sksamuel.elastic4s.testkit.ElasticSugar

class SearchShowTest extends WordSpec with Matchers with ElasticSugar {

  import ElasticDsl._

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
        } suggestions {
          term suggestion "names" maxEdits 3 field "name" mode SuggestMode.Always text "jon show"
        }
      }
      request
        .show shouldBe """{
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
                       |  },
                       |  "suggest" : {
                       |    "names" : {
                       |      "text" : "jon show",
                       |      "term" : {
                       |        "field" : "name",
                       |        "suggest_mode" : "always",
                       |        "max_edits" : 3
                       |      }
                       |    }
                       |  }
                       |}""".stripMargin
    }
  }
}
