package com.sksamuel.elastic4s.validate

import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.{Matchers, WordSpec}

class ValidateShowTest extends WordSpec with Matchers with ElasticSugar {

  "Search" should {
    "have a show typeclass implementation" in {
      val request = {
        validateIn("gameofthrones" / "characters").query {
          bool {
            should {
              termQuery("name", "snow")
            }.must {
              matchQuery("location", "the wall")
            }
          }
        } explain true
      }
      request.show.trim shouldBe """[[gameofthrones]][characters], query[{
                                   |  "bool" : {
                                   |    "must" : [
                                   |      {
                                   |        "match" : {
                                   |          "location" : {
                                   |            "query" : "the wall",
                                   |            "operator" : "OR",
                                   |            "prefix_length" : 0,
                                   |            "max_expansions" : 50,
                                   |            "fuzzy_transpositions" : true,
                                   |            "lenient" : false,
                                   |            "zero_terms_query" : "NONE",
                                   |            "boost" : 1.0
                                   |          }
                                   |        }
                                   |      }
                                   |    ],
                                   |    "should" : [
                                   |      {
                                   |        "term" : {
                                   |          "name" : {
                                   |            "value" : "snow",
                                   |            "boost" : 1.0
                                   |          }
                                   |        }
                                   |      }
                                   |    ],
                                   |    "disable_coord" : false,
                                   |    "adjust_pure_negative" : true,
                                   |    "boost" : 1.0
                                   |  }
                                   |}], explain:true, rewrite:false""".stripMargin.trim
    }
  }
}
