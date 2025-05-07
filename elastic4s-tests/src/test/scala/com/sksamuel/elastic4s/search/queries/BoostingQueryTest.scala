package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class BoostingQueryTest extends AnyFlatSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("fonts")
    }.await
  }

  client.execute {
    createIndex("fonts")
  }.await

  client.execute {
    bulk(
      indexInto("fonts").fields("name" -> "helvetica"),
      indexInto("fonts").fields("name" -> "arial"),
      indexInto("fonts").fields("name" -> "verdana")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "boosting query" should "boost" in {
    client.execute {
      search("fonts").query {
        boostingQuery(matchAllQuery(), "verdana", 0.5)
      }
    }.await.result.hits.hits.map(_.sourceField("name")) shouldBe Array("helvetica", "arial", "verdana")

    client.execute {
      search("fonts").query {
        boostingQuery(matchAllQuery(), "arial", 0.5)
      }
    }.await.result.hits.hits.map(_.sourceField("name")) shouldBe Array("helvetica", "verdana", "arial")
  }

  "deprecated boosting query" should "still work" in {
    client.execute {
      search("fonts").query {
        boostingQuery(matchAllQuery(), "verdana").negativeBoost(0.5)
      }
    }.await.result.hits.hits.map(_.sourceField("name")) shouldBe Array("helvetica", "arial", "verdana")

    client.execute {
      search("fonts").query {
        boostingQuery(matchAllQuery(), "arial").negativeBoost(0.5)
      }
    }.await.result.hits.hits.map(_.sourceField("name")) shouldBe Array("helvetica", "verdana", "arial")
  }
}
