package com.sksamuel.elastic4s.search.highlight

import com.sksamuel.elastic4s.testkit.{ElasticSugar, SharedElasticSugar}
import org.scalatest.{Matchers, WordSpec}

class HighlightTcpTest extends WordSpec with SharedElasticSugar with Matchers {

  client.execute {
    createIndex("intros").mappings(
      mapping("tv").fields(
        textField("name"),
        textField("text")
      )
    )
  }.await

  client.execute {
    indexInto("intros/tv")
      .fields(
        "name" -> "star trek",
        "text" -> "Space, the final frontier. These are the voyages of the starship Enterprise. Its continuing mission: to explore strange new worlds, to seek out new life and new civilisations, to boldly go where no one has gone before."
      )
  }.await

  refresh("intros")
  blockUntilCount(1, "intros")

  "highlighting" should {
    "highlight selected words" in {

      val resp = client.execute {
        search("intros").termQuery("text", "frontier").highlighting(
          highlight("text").requireFieldMatch(false)
        )
      }.await

      resp.size shouldBe 1

      val fragments = resp.hits.head.highlightFields("text").fragments()
      fragments.size shouldBe 1
      fragments
        .head
        .string() shouldBe "Space, the final <em>frontier</em>. These are the voyages of the starship Enterprise. Its continuing mission"
    }
    "use fragment size" in {
      val resp = client.execute {
        search("intros") query "new" highlighting (
          highlight("text").requireFieldMatch(false) fragmentSize 15
          )
      }.await
      val fragments = resp.hits.head.highlightFields("text").fragments()
      fragments.size shouldBe 3
      fragments(0).string() shouldBe " <em>new</em> worlds, to"
      fragments(1).string() shouldBe " seek out <em>new</em>"
      fragments(2).string() shouldBe " life and <em>new</em>"
    }
    "use number of fragments size" in {
      val resp = client.execute {
        search("intros") query "text:new" highlighting (
          highlight("text") fragmentSize 5 numberOfFragments 2
          )
      }.await
      val fragments = resp.hits.head.highlightFields("text").fragments()
      fragments.size shouldBe 2
    }
    "use no match size" in {
      val resp = client.execute {
        search("intros") query "trek" highlighting (
          highlight("text") noMatchSize 50
          )
      }.await
      val fragments = resp.hits.head.highlightFields("text").fragments()
      fragments.size shouldBe 1
      fragments(0).string() shouldBe "Space, the final frontier. These are the voyages"
    }
    "use pre tags" in {
      val resp = client.execute {
        search("intros") query matchQuery("text", "frontier") highlighting (
          highlight("text") fragmentSize 20 preTag "<picard>"
          )
      }.await
      val fragments = resp.hits.head.highlightFields("text").fragments()
      fragments.size shouldBe 1
      fragments(0).string.trim shouldBe "<picard>frontier</em>. These are"
    }
    "use post tags" in {
      val resp = client.execute {
        search("intros" / "tv") query matchQuery("text", "frontier") highlighting (
          highlight("text") fragmentSize 20 preTag "<riker>"
          )
      }.await
      val fragments = resp.hits.head.highlightFields("text").fragments()
      fragments.size shouldBe 1
      fragments(0).string.trim shouldBe "<riker>frontier</em>. These are"
    }
    "use highlight query" in {
      val resp = client.execute {
        search("intros" / "tv") query matchQuery("text", "frontier") highlighting (
          highlight("text") fragmentSize 20 query matchQuery("text", "life")
          )
      }.await
      val fragments = resp.hits.head.highlightFields("text").fragments()
      fragments.size shouldBe 1
      fragments(0).string.trim shouldBe "out new <em>life</em> and"
    }
  }
}
