package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.testkit.{DiscoveryLocalNodeProvider, ElasticSugar}
import org.scalatest.{FlatSpec, Matchers}

class FieldNamesFieldTest extends FlatSpec with Matchers with ElasticSugar with DiscoveryLocalNodeProvider {

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
    )
  }.await

  blockUntilCount(2, "space")

  "_field_names" should "index the names of every field in a document that contains any value other than null" in {

    client.execute {
      search("space").query(termsQuery("_field_names", "name"))
    }.await.totalHits shouldBe 2

    client.execute {
      search("space").query(termsQuery("_field_names", "location"))
    }.await.totalHits shouldBe 1

    client.execute {
      search("space").query(fieldNamesQuery("name"))
    }.await.totalHits shouldBe 2

    client.execute {
      search("space").query(fieldNamesQuery("location"))
    }.await.totalHits shouldBe 1
  }
}
