package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.{DockerTests, ElasticDsl}
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class MatchQueryTest extends WordSpec with Matchers with DockerTests with ElasticDsl {

  Try {
    http.execute {
      ElasticDsl.deleteIndex("elite")
    }.await
  }

  http.execute {
    bulk(
      indexInto("elite/ships") fields("name" -> "vulture", "manufacturer" -> "Core Dynamics"),
      indexInto("elite/ships") fields("name" -> "sidewinder", "manufacturer" -> "Core Dynamics"),
      indexInto("elite/ships") fields("name" -> "cobra mark 3", "manufacturer" -> "Core Dynamics")
    ).refreshImmediately
  }.await

  "a match query" should {
    "use same analyzer as inputs" in {
      val resp = http.execute {
        search("elite" / "ships") query {
          matchQuery("name", "VULTURE")
        }
      }.await.right.get.result
      resp.totalHits shouldBe 1
    }
  }
}
