package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DualClientTests
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class RawQueryTest extends WordSpec with Matchers with ElasticDsl with DualClientTests {

  override protected def beforeRunTests(): Unit = {

    Try {
      execute {
        deleteIndex("rawquerytest")
      }.await
    }

    execute {
      bulk(
        indexInto("rawquerytest/paris").fields("landmark" -> "montmarte", "arrondissement" -> "18"),
        indexInto("rawquerytest/paris").fields("landmark" -> "le tower eiffel", "arrondissement" -> "7")
      ).immediateRefresh()
    }.await
  }

  "raw query" should {
    "work!" in {
      execute {
        search("*").types("paris") limit 5 rawQuery {
          """{ "prefix": { "landmark": { "prefix": "montm" } } }"""
        }
      }.await.totalHits shouldBe 1
    }
  }
}
