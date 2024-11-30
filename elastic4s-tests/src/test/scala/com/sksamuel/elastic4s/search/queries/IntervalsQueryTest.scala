package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.queries.{AllOf, AnyOf, IntervalsQuery, Match}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class IntervalsQueryTest extends AnyFlatSpec with Matchers with DockerTests {
  Try {
    client.execute {
      deleteIndex("intervalstest")
    }.await
  }

  client.execute {
    createIndex("intervalstest")
  }.await

  client.execute {
    bulk(
      indexInto("intervalstest").fields(
        "my_text" -> "my favorite food is cold porridge"
      ),
      indexInto("intervalstest").fields(
        "my_text" -> "when it's cold my favorite food is porridge"
      )
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "intervals query" should "work as in the elasticsearch docs" in {
    val resp = client.execute {
      search("intervalstest").query {
        IntervalsQuery(
          "my_text",
          AllOf(List(
            Match(query = "favorite food").maxGaps(0).ordered(true),
            AnyOf(intervals =
              List(
                Match(query = "hot water"),
                Match(query = "cold porridge")
              )
            )
          )).ordered(true)
        )
      }
    }.await.result

    resp.totalHits shouldBe 1
    resp.hits.hits.head.sourceField("my_text") shouldBe "my favorite food is cold porridge"
  }
}
