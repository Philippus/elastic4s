package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class ExistsQueryTest extends WordSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("person")
    }.await
  }

  client.execute(
    bulk(
      indexInto("person") fields(
        "name" -> "reese",
        "weapon" -> "revolver"
      ),
      indexInto("person") fields(
        "name" -> "finch",
        "weapon" -> "computer"
      )
    ).refreshImmediately
  ).await

  "exists query" should {
    "match non-null fields" in {
      client.execute {
        search("person") postFilter {
          existsQuery("name")
        }
      }.await.result.totalHits shouldBe 2
    }
    "not match null fields" in {
      client.execute {
        search("person") postFilter {
          existsQuery("place")
        }
      }.await.result.totalHits shouldBe 0
    }
  }
}
