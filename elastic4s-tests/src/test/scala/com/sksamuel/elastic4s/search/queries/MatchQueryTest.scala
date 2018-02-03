package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class MatchQueryTest
  extends FlatSpec
    with DockerTests
    with Matchers {

  Try {
    http.execute {
      deleteIndex("units")
    }.await
  }

  http.execute {
    createIndex("units")
  }.await

  http.execute {
    bulk(
      indexInto("units/base") fields("name" -> "candela", "scientist.name" -> "Jules Violle", "scientist.country" -> "France")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "a match query" should "support selecting nested properties" in {

    val resp = http.execute {
      search("units") query matchQuery("name", "candela") sourceInclude "scientist.name"
    }.await.result

    resp.hits.hits.head.sourceAsMap shouldBe Map("scientist.name" -> "Jules Violle")
  }
}
