package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class MatchQueryTest
  extends FlatSpec
    with DockerTests
    with Matchers {

  Try {
    client.execute {
      deleteIndex("units")
    }.await
  }

  client.execute {
    createIndex("units")
  }.await

  client.execute {
    bulk(
      indexInto("units/base") fields("name" -> "candela", "scientist.name" -> "Jules Violle", "scientist.country" -> "France")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "a match query" should "support selecting nested properties" in {

    val resp = client.execute {
      search("units") query matchQuery("name", "candela") sourceInclude "scientist.name"
    }.await.result

    resp.hits.hits.head.sourceAsMap shouldBe Map("scientist.name" -> "Jules Violle")
  }
}
