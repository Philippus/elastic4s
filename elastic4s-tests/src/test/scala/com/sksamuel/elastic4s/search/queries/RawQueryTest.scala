package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class RawQueryTest extends WordSpec with Matchers with DockerTests {

  Try {
    http.execute {
      deleteIndex("rawquerytest")
    }.await
  }

  http.execute {
    bulk(
      indexInto("rawquerytest/paris").fields("landmark" -> "montmarte", "arrondissement" -> "18"),
      indexInto("rawquerytest/paris").fields("landmark" -> "le tower eiffel", "arrondissement" -> "7")
    ).refreshImmediately
  }.await

  "raw query" should {
    "work!" ignore {
      http.execute {
        search("*").types("paris") limit 5 rawQuery {
          """{ "prefix": { "landmark": { "prefix": "montm" } } }"""
        }
      }.await.result.totalHits shouldBe 1
    }
  }
}
