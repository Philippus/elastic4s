package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.testkit.{DiscoveryLocalNodeProvider, ElasticSugar}
import org.scalatest.{Matchers, WordSpec}

class SearchShowTest extends WordSpec with Matchers with DiscoveryLocalNodeProvider with ElasticDsl {

  "Search" should {
    "have a show typeclass implementation" in {

      val request = {
        search("gameofthrones" / "characters") query {
          boolQuery().
            should(
              termQuery("name", "snow")
            ).must(
            matchQuery("location", "the wall")
          )
        }
      }

      request.show.trim shouldBe
        """{
          |  "query" : {
          |    "bool" : {
          |      "must" : [
          |        {
          |          "match" : {
          |            "location" : {
          |              "query" : "the wall",
          |              "operator" : "OR",
          |              "prefix_length" : 0,
          |              "max_expansions" : 50,
          |              "fuzzy_transpositions" : true,
          |              "lenient" : false,
          |              "zero_terms_query" : "NONE",
          |              "boost" : 1.0
          |            }
          |          }
          |        }
          |      ],
          |      "should" : [
          |        {
          |          "term" : {
          |            "name" : {
          |              "value" : "snow",
          |              "boost" : 1.0
          |            }
          |          }
          |        }
          |      ],
          |      "adjust_pure_negative" : true,
          |      "boost" : 1.0
          |    }
          |  }
          |}""".stripMargin.trim
    }
  }
}
