package com.sksamuel.elastic4s.search.queries

import scala.util.Try

import com.sksamuel.elastic4s.requests.searches.queries.NoopQuery
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class NoopQueryTest extends AnyFlatSpec with Matchers with DockerTests {
  Try {
    client.execute {
      deleteIndex("noop")
    }.await
  }

  client.execute {
    createIndex("noop")
  }.await

  "noop query" should "not error out when executed" in {
    client.execute {
      search("noop").query {
        boolQuery().should(Seq(NoopQuery))
      }
    }.await.result
  }

  "it" should "not error out when executed in a sequence" in {
    client.execute {
      search("noop").query {
        boolQuery().should(Seq(NoopQuery, NoopQuery))
      }
    }.await.result
  }
}
