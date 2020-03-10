package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.testkit.DockerTests

import scala.util.Try
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RawQueryTest extends AnyWordSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("rawquerytest")
    }.await
  }

  client.execute {
    bulk(
      indexInto("rawquerytest").fields("landmark" -> "montmarte", "arrondissement" -> "18"),
      indexInto("rawquerytest").fields("landmark" -> "le tower eiffel", "arrondissement" -> "7")
    ).refreshImmediately
  }.await

  "raw query" should {
    "work!" ignore {
      client.execute {
        search("*") limit 5 rawQuery {
          """{ "prefix": { "landmark": { "prefix": "montm" } } }"""
        }
      }.await.result.totalHits shouldBe 1
    }
  }
}
