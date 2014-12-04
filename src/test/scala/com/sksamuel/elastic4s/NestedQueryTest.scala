package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType.NestedType
import org.scalatest.{ Matchers, FreeSpec }

class NestedQueryTest extends FreeSpec with Matchers with ElasticSugar {

  client.execute {
    create index "nested" mappings {
      "show" as {
        "actor" typed NestedType
      }
    }
  }.await

  client.execute(
    index into "nested/show" fields (
      "name" -> "game of thrones",
      "actor" -> Seq(
        Map("name" -> "peter dinklage", "birthplace" -> "Morristown"),
        Map("name" -> "pedro pascal", "birthplace" -> "Santiago")
      )
    )
  ).await

  refresh("nested")
  blockUntilCount(1, "nested")

  "nested object" - {
    "should be searchable by nested field" in {
      val resp1 = client.execute {
        search in "nested/show" query nestedQuery("actor").query(termQuery("actor.name" -> "dinklage"))
      }.await
      resp1.getHits.totalHits() shouldEqual 1

      val resp2 = client.execute {
        search in "nested/show" query nestedQuery("actor").query(termQuery("actor.name" -> "simon"))
      }.await
      resp2.getHits.totalHits() shouldEqual 0
    }
  }
}
