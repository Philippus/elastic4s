//package com.sksamuel.elastic4s.search
//
//import com.sksamuel.elastic4s.JsonSugar
//import com.sksamuel.elastic4s.http.ElasticDsl
//import org.scalatest.{Matchers, WordSpec}
//
//class MultiSearchShowTest extends WordSpec with Matchers with JsonSugar with ElasticDsl {
//
//  "MultiSearch" should {
//    "have a show typeclass implementation" in {
//      val request = {
//        multi(
//          search("gameofthrones" / "characters") query termQuery("name", "snow"),
//          search("gameofthrones" / "characters") query termQuery("name", "tyrion"),
//          search("gameofthrones" / "characters") query termQuery("name", "brienne")
//        )
//      }
//      println(request.show)
//      request.show should matchJson( """[
//                                       |{
//                                       |  "query" : {
//                                       |    "term" : {
//                                       |      "name" : {
//                                       |        "value": "snow",
//                                       |        "boost" : 1.0
//                                       |      }
//                                       |    }
//                                       |  }
//                                       |},
//                                       |{
//                                       |  "query" : {
//                                       |    "term" : {
//                                       |      "name": {
//                                       |        "value" : "tyrion",
//                                       |        "boost" : 1.0
//                                       |      }
//                                       |    }
//                                       |  }
//                                       |},
//                                       |{
//                                       |  "query" : {
//                                       |    "term" : {
//                                       |      "name" : {
//                                       |        "value" : "brienne",
//                                       |        "boost" : 1.0
//                                       |      }
//                                       |    }
//                                       |  }
//                                       |}]""".stripMargin)
//    }
//  }
//}
