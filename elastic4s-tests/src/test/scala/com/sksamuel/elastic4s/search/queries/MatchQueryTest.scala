package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class MatchQueryTest
  extends AnyFlatSpec
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
      indexInto("units") fields("name" -> "candela", "scientist.name" -> "Jules Violle", "scientist.country" -> "France")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "a match query" should "support selecting nested properties" in {

    val resp = client.execute {
      search("units") query matchQuery("name", "candela") sourceInclude "scientist.name"
    }.await.result

    resp.hits.hits.head.sourceAsMap shouldBe Map("scientist.name" -> "Jules Violle")
  }

  "a match query" should "support excluding nested properties" in {

    val resp = client.execute {
      search("units") query matchQuery("name", "candela") sourceExclude "scientist.name"
    }.await.result

    resp.hits.hits.head.sourceAsMap shouldBe Map("name" -> "candela", "scientist.country" -> "France")
  }

  "a match query" should "support including and excluding nested properties" in {

    val resp = client.execute {
      search("units") query matchQuery("name", "candela") sourceInclude "*name" sourceExclude "scientist.*"
    }.await.result

    resp.hits.hits.head.sourceAsMap shouldBe Map("name" -> "candela")
  }
}
