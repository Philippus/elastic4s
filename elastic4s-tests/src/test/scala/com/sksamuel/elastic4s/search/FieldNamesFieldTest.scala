package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.DockerTests
import com.sksamuel.elastic4s.http.ElasticDsl
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class FieldNamesFieldTest extends FlatSpec with Matchers with DockerTests {

  Try {
    http.execute {
      ElasticDsl.deleteIndex("space")
    }.await
  }

  http.execute {
    createIndex("space")
  }.await

  http.execute {
    bulk(
      indexInto("space/dwarf").fields(
        "name" -> "Ceres"
      ),
      indexInto("space/dwarf").fields(
        "name" -> "Pluto",
        "location" -> "solar system"
      )
    ).refreshImmediately
  }.await

  // seems to be broken in 6.1.0
  "_field_names" should "index the names of every field in a document that contains any value other than null" in {

    http.execute {
      search("space").query(existsQuery("name"))
    }.await.right.get.result.totalHits shouldBe 2

    http.execute {
      search("space").query(existsQuery("location"))
    }.await.right.get.result.totalHits shouldBe 1

    http.execute {
      search("space").query(fieldNamesQuery("name"))
    }.await.right.get.result.totalHits shouldBe 2

    http.execute {
      search("space").query(fieldNamesQuery("location"))
    }.await.right.get.result.totalHits shouldBe 1
  }
}
