package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.{Matchers, WordSpec}

class MultiSearchShowTest extends WordSpec with Matchers with ElasticSugar {

  import ElasticDsl._

  "MultiSearch" should {
    "have a show typeclass implementation" in {
      val request = {
        multi(
          search in "gameofthrones" / "characters" query termQuery("name", "snow"),
          search in "gameofthrones" / "characters" query termQuery("name", "tyrion"),
          search in "gameofthrones" / "characters" query termQuery("name", "brienne")
        )
      }
      request.show shouldBe """[
                              |{
                              |  "query" : {
                              |    "term" : {
                              |      "name" : "snow"
                              |    }
                              |  }
                              |},
                              |{
                              |  "query" : {
                              |    "term" : {
                              |      "name" : "tyrion"
                              |    }
                              |  }
                              |},
                              |{
                              |  "query" : {
                              |    "term" : {
                              |      "name" : "brienne"
                              |    }
                              |  }
                              |}]""".stripMargin
    }
  }
}
