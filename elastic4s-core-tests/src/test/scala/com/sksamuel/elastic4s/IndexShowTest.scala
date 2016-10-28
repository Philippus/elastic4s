package com.sksamuel.elastic4s2

import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.{Matchers, WordSpec}

class IndexShowTest extends WordSpec with Matchers with ElasticSugar {

  import ElasticDsl._

  "Index" should {
    "have a show typeclass implementation" in {
      val request = index into "gameofthrones" / "characters" fields("name" -> "jon snow", "location" -> "the wall")
      request.show shouldBe """{
                              |  "name" : "jon snow",
                              |  "location" : "the wall"
                              |}""".stripMargin
    }
  }
}
