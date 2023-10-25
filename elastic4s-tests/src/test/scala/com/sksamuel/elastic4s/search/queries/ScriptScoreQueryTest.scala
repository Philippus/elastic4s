package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Try

class ScriptScoreQueryTest extends AnyWordSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("person")
    }.await
  }

  client.execute(
    bulk(
      indexInto("person") fields(
        "name" -> "reese",
        "age" -> 1.0
      ),
      indexInto("person") fields(
        "name" -> "finch",
        "age" -> 1.0
      ),
      indexInto("person") fields(
        "name" -> "finch",
        "age" -> 2.0
      ),
      indexInto("person") fields(
        "name" -> "finch",
        "age" -> 3.0
      )
    ).refreshImmediately
  ).await

  "script score query" should {
    "filter by query" in {
      client.execute {
        search("person") postFilter {
          scriptScoreQuery(termQuery("name", "finch"))
            .script(Script("doc['age'].value % 3"))
        }
      }.await.result.totalHits shouldBe 3
    }
    "sort by score" in {
      client.execute {
        search("person") postFilter {
          scriptScoreQuery(termQuery("name", "finch"))
            .script(Script("doc['age'].value % 3"))
        }
      }.await.result.hits.hits.map(_.sourceAsString) shouldBe Array(2.0, 1.0, 0.0)
    }
  }
}
