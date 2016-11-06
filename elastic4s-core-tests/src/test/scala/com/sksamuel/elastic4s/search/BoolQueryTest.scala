package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.{FlatSpec, Matchers}

class BoolQueryTest extends FlatSpec with ElasticSugar with Matchers {

  client.execute {
    createIndex("fonts")
  }.await

  client.execute {
    bulk(
      indexInto("fonts/family").fields("name" -> "helvetica", "style" -> "sans"),
      indexInto("fonts/family").fields("name" -> "helvetica 2", "style" -> "serif"),
      indexInto("fonts/family").fields("name" -> "arial", "style" -> "serif"),
      indexInto("fonts/family").fields("name" -> "verdana", "style" -> "serif"),
      indexInto("fonts/family").fields("name" -> "times new roman", "style" -> "serif"),
      indexInto("fonts/family").fields("name" -> "roman comic", "style" -> "comic"),
      indexInto("fonts/family").fields("name" -> "comic sans", "style" -> "comic")

    )
  }

  blockUntilCount(4, "fonts")

  "bool query" should "support must and not" in {
    val resp = client.execute {
      search("fonts/family").query {
        boolQuery().must("helvetica").not("serif")
      }
    }.await

    resp.totalHits shouldBe 1
    resp.hits.head.sourceField("style") shouldBe "sans"
  }

  it should "support not" in {
    val resp = client.execute {
      search("fonts/family").query {
        boolQuery().not("sans")
      }
    }.await

    resp.totalHits shouldBe 5
    resp.hits.head.sourceField("style") shouldBe "serif"
  }

  it should "support must" in {
    val resp = client.execute {
      search("fonts/family").query {
        boolQuery().must("roman")
      }
    }.await

    resp.totalHits shouldBe 2
    resp.hits.map(_.sourceField("name")).toSet shouldBe Set("times new roman", "roman comic")
  }

  it should "support or using should" in {
    val resp = client.execute {
      search("fonts/family").query {
        boolQuery().should(
          matchPhraseQuery("name", "times new roman"),
          matchPhraseQuery("name", "comic sans")
        )
      }
    }.await

    resp.totalHits shouldBe 2
    resp.hits.map(_.sourceField("name")).toSet shouldBe Set("times new roman", "comic sans")
  }
}
