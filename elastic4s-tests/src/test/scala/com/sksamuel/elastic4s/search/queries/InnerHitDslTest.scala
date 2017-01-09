//package com.sksamuel.elastic4s.search.queries
//
//import com.sksamuel.elastic4s.ElasticDsl
//import org.scalatest.WordSpec
//
//class InnerHitDslTest extends WordSpec with ElasticDsl {
//
//  "top level inner hits" should {
//    "compile" in {
//      search in "index" / "type" inner (
//        inner hit "name" path "path",
//        inner hit "name" `type` "type"
//      )
//    }
//  }
//}
