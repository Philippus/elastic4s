package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class BoolQueryTest extends FlatSpec with Matchers with DockerTests {

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
      indexInto("fonts/family").fields("name" -> "helvetica", "style" -> "sans"),
      indexInto("fonts/family").fields("name" -> "helvetica modern", "style" -> "serif"),
      indexInto("fonts/family").fields("name" -> "arial", "style" -> "serif"),
      indexInto("fonts/family").fields("name" -> "verdana", "style" -> "serif"),
      indexInto("fonts/family").fields("name" -> "times new roman", "style" -> "serif"),
      indexInto("fonts/family").fields("name" -> "roman comic", "style" -> "comic"),
      indexInto("fonts/family").fields("name" -> "comic sans", "style" -> "comic")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "bool query" should "support must and not" in {
    val resp = client.execute {
      search("fonts/family").query {
        boolQuery().must("helvetica").not("serif")
      }
    }.await.result

    resp.totalHits shouldBe 1
    resp.hits.hits.head.sourceField("style") shouldBe "sans"
  }

  it should "support multiple must queries" in {
    val resp = client.execute {
      search("fonts/family").query {
        boolQuery().must("times", "new")
      }
    }.await.result

    resp.totalHits shouldBe 1
    resp.hits.hits.head.sourceField("name") shouldBe "times new roman"
  }

  it should "support not" in {
    val resp = client.execute {
      search("fonts/family").query {
        boolQuery().not("sans")
      }
    }.await.result

    resp.totalHits shouldBe 5
    resp.hits.hits.map(_.sourceField("style")).toSet shouldBe Set("comic", "serif")
  }

  it should "support must" in {
    val resp = client.execute {
      search("fonts/family").query {
        boolQuery().must("roman")
      }
    }.await.result

    resp.totalHits shouldBe 2
    resp.hits.hits.map(_.sourceField("name")).toSet shouldBe Set("times new roman", "roman comic")
  }

  it should "support or using should" in {
    val resp = client.execute {
      search("fonts/family").query {
        boolQuery().should(
          matchPhraseQuery("name", "times new roman"),
          matchPhraseQuery("name", "comic sans")
        )
      }
    }.await.result

    resp.totalHits shouldBe 2
    resp.hits.hits.map(_.sourceField("name")).toSet shouldBe Set("times new roman", "comic sans")
  }
}
