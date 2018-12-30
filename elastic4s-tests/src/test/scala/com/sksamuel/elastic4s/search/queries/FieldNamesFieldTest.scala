package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class FieldNamesFieldTest extends FlatSpec with Matchers with DockerTests {

  Try {
    client.execute {
      ElasticDsl.deleteIndex("space")
    }.await
  }

  client.execute {
    createIndex("space")
  }.await

  client.execute {
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

    client.execute {
      search("space").query(existsQuery("name"))
    }.await.result.totalHits shouldBe 2

    client.execute {
      search("space").query(existsQuery("location"))
    }.await.result.totalHits shouldBe 1

    client.execute {
      search("space").query(existsQuery("name"))
    }.await.result.totalHits shouldBe 2

    client.execute {
      search("space").query(existsQuery("location"))
    }.await.result.totalHits shouldBe 1
  }
}
