package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import com.sksamuel.elastic4s.testkit.{DualClient, DualElasticSugar}
import org.scalatest.{Matchers, WordSpec}

class RawQueryTest extends WordSpec with Matchers with ElasticDsl with DualElasticSugar with DualClient {

  override protected def beforeRunTests(): Unit = {
    execute {
      bulk(
        indexInto("rawquerytest/paris").fields("landmark" -> "montmarte", "arrondissement" -> "18"),
        indexInto("rawquerytest/paris").fields("landmark" -> "le tower eiffel", "arrondissement" -> "7"),
        indexInto("rawquerytest/tokyo").fields("landmark" -> "tokyo tower"),
        indexInto("rawquerytest/tokyo").fields("landmark" -> "meiji shrine")
      )
    }.await

    blockUntilCount(2, "rawquerytest")
  }

  "raw query" should {
    "work!" in {
      execute {
        search("*").types("paris") limit 5 rawQuery {
          """{ "prefix": { "landmark": { "prefix": "montm" } } }"""
        }
      }.await.totalHits shouldBe 1
    }

    "work for multiple types" in {
      execute {
        search("*").types("tokyo", "paris") limit 5 rawQuery {
          """{ "term": { "landmark": "tower" } }"""
        }
      }.await.totalHits shouldBe 2
    }
  }
}
