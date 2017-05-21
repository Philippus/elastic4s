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
        indexInto("rawquerytest/paris").fields("landmark" -> "le tower eiffel", "arrondissement" -> "7"),
        indexInto("rawquerytest/tokyo").fields("landmark" -> "tokyo tower"),
        indexInto("rawquerytest/tokyo").fields("landmark" -> "meiji shrine")
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

    "work for multiple types" in {
      execute {
        search("*").types("tokyo", "paris") limit 5 rawQuery {
          """{ "term": { "landmark": "tower" } }"""
        }
      }.await.totalHits shouldBe 2
    }
  }
}
