package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.testkit.ElasticSugar
import com.sksamuel.elastic4s.{ElasticDsl, JsonSugar}
import org.scalatest.{Matchers, WordSpec}

class MultiSearchShowTest extends WordSpec with Matchers with JsonSugar with ElasticDsl {

  "MultiSearch" should {
    "have a show typeclass implementation" in {
      val request = {
        multi(
          search in "gameofthrones" / "characters" query termQuery("name", "snow"),
          search in "gameofthrones" / "characters" query termQuery("name", "tyrion"),
          search in "gameofthrones" / "characters" query termQuery("name", "brienne")
        )
      }
      println(request.show)
      request.show should matchJson( """[
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
                                       |}]""")
    }
  }
}
