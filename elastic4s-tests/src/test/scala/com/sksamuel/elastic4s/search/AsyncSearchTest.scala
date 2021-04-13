package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class AsyncSearchTest extends AnyFlatSpec with Matchers with DockerTests {

  Try {
    client.execute {
      ElasticDsl.deleteIndex("colors")
    }.await
  }

  client.execute {
    createIndex("colors").mapping(
      properties(
        textField("name").fielddata(true)
      )
    )
  }.await

  client.execute {
    bulk(
      indexInto("colors").fields("name" -> "green").id("1"),
      indexInto("colors").fields("name" -> "blue").id("2"),
      indexInto("colors").fields("name" -> "red").id("3")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "an search query" should "find an indexed document that matches a term query" in {
    client.execute {
      asyncSearch("chess") query termQuery("name", "pawn")
    }.await.result.totalHits shouldBe 1
  }
}
