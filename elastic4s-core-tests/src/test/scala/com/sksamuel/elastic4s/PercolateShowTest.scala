package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.{Matchers, WordSpec}

class PercolateShowTest extends WordSpec with Matchers with ElasticSugar {

  import ElasticDsl._

  "PercolateDefinition" should {
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

  "RegisterDefinition" should {
    "have a show typeclass implementation" in {
      val request = register id 14 into "star_trek" query termQuery("name", "kirk")
      request.show shouldBe """{
                              |  "query" : {
                              |    "term" : {
                              |      "name" : "kirk"
                              |    }
                              |  }
                              |}""".stripMargin
    }
  }
}
