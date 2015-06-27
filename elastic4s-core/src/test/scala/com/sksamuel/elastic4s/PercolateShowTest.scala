package com.sksamuel.elastic4s

import org.scalatest.{Matchers, WordSpec}

class PercolateShowTest extends WordSpec with Matchers with ElasticSugar {

  import ElasticDsl._

  "Index" should {
    "have a show typeclass implementation" in {
      val request = percolate in "star_trek" / " captains" doc "name" -> "cook" query {
        termQuery("color" -> "blue")
      }
      request.show shouldBe """{
                              |  "query" : {
                              |    "term" : {
                              |      "color" : "blue"
                              |    }
                              |  },
                              |  "doc" : {
                              |    "name" : "cook"
                              |  }
                              |}""".stripMargin
    }
  }
}
