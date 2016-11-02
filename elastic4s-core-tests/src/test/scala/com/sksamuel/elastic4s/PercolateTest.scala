//package com.sksamuel.elastic4s
//
//import com.sksamuel.elastic4s.mappings.FieldType.StringType
//import com.sksamuel.elastic4s.testkit.ElasticSugar
//import org.scalatest.mock.MockitoSugar
//import org.scalatest.{ FlatSpec, Matchers }
//import com.sksamuel.elastic4s.ElasticDsl._
//
///** @author Stephen Samuel */
//class PercolateTest extends FlatSpec with Matchers with MockitoSugar with ElasticSugar {
//
//  client.execute {
//    create index "percolate" mappings {
//      "teas" as {
//        "flavour" typed StringType
//      }
//    }
//  }.await
//
//  client.execute {
//    register id "a" into "percolate" query {
//      termQuery("flavour", "assam")
//    }
//  }.await
//
//  client.execute {
//    register id "b" into "percolate" query {
//      termQuery("flavour", "earl")
//    }
//  }.await
//
//  client.execute {
//    register id "c" into "percolate" query {
//      termQuery("flavour", "darjeeling")
//    }
//  }.await
//
//  "a percolate request" should "return queries that match the document" in {
//
//    val matches = client.execute {
//      percolate in "percolate/teas" doc "flavour" -> "assam"
//    }.await.getMatches
//
//    matches.size shouldBe 1
//    matches(0).getId.string shouldBe "a"
//  }
//
//  "a percolate request for existing document" should "return queries that match the document" in {
//    val _id = client.execute {
//      index into "percolate/teas" source """{"flavour" : "earl"}"""
//    }.await.id
//
//    refresh("percolate").await
//
//    val matches = client.execute {
//      percolate in "percolate/teas" id _id
//    }.await.getMatches
//
//    matches.size shouldBe 1
//    matches(0).getId.string shouldBe "b"
//
//  }
//
//
//}
