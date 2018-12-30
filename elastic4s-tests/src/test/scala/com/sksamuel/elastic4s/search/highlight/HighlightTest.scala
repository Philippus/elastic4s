package com.sksamuel.elastic4s.search.highlight

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class HighlightTest extends WordSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("intros")
    }.await
  }

  client.execute {
    createIndex("intros").mappings(
      mapping("tv").fields(
        textField("name").stored(true),
        textField("text").stored(true)
      )
    )
  }.await

  client.execute {
    indexInto("intros/tv")
      .fields(
        "name" -> "star trek",
        "text" -> "Space, the final frontier. These are the voyages of the starship Enterprise. Its continuing mission: to explore strange new worlds, to seek out new life and new civilisations, to boldly go where no one has gone before."
      ).refresh(RefreshPolicy.Immediate)
  }.await

  "highlighting" should {
    "ignore missing highlight" in {
      client.execute {
        search("intros").matchAllQuery().limit(1)
      }.await.result.hits.hits.map(_.highlight) shouldBe Array(Map.empty)
    }
    "highlight selected words" in {

      val resp = client.execute {
        search("intros").matchQuery("text", "frontier").highlighting(
          highlight("text")
        )
      }.await.result

      resp.size shouldBe 1

      val fragments = resp.hits.hits.head.highlightFragments("text")
      fragments.size shouldBe 1
      fragments.head shouldBe
        "Space, the final <em>frontier</em>. These are the voyages of the starship Enterprise."
    }
    "use fragment size" in {
      val resp = client.execute {
        search("intros") query "new" highlighting (
          highlight("text").requireFieldMatch(false) fragmentSize 15
          )
      }.await.result
      val fragments = resp.hits.hits.head.highlightFragments("text")
      fragments.size shouldBe 3
      fragments.head shouldBe "explore strange <em>new</em>"
      fragments(1) shouldBe "worlds, to seek out <em>new</em>"
      fragments(2) shouldBe "life and <em>new</em> civilisations"
    }
    "use number of fragments size" in {
      val resp = client.execute {
        search("intros") query "text:new" highlighting (
          highlight("text") fragmentSize 5 numberOfFragments 2
          )
      }.await.result
      val fragments = resp.hits.hits.head.highlightFragments("text")
      fragments.size shouldBe 2
    }
    "use no match size" in {
      val resp = client.execute {
        search("intros") query "trek" highlighting (
          highlight("text") noMatchSize 50
          )
      }.await.result
      val fragments = resp.hits.hits.head.highlightFragments("text")
      fragments.size shouldBe 1
      fragments.head shouldBe "Space, the final frontier. These are the voyages of"
    }
    "use pre tags" in {
      val resp = client.execute {
        search("intros") query matchQuery("text", "frontier") highlighting (
          highlight("text") fragmentSize 20 preTag "<picard>"
          )
      }.await.result
      val fragments = resp.hits.hits.head.highlightFragments("text")
      fragments.size shouldBe 1
      fragments.head.trim shouldBe "Space, the final <picard>frontier</em>"
    }
    "use post tags" in {
      val resp = client.execute {
        search("intros" / "tv") query matchQuery("text", "frontier") highlighting (
          highlight("text") fragmentSize 20 postTag "<riker>"
          )
      }.await.result
      val fragments = resp.hits.hits.head.highlightFragments("text")
      fragments.size shouldBe 1
      fragments.head.trim shouldBe "Space, the final <em>frontier<riker>"
    }
    "use highlight query" in {
      val resp = client.execute {
        search("intros" / "tv") query matchQuery("text", "frontier") highlighting (
          highlight("text") fragmentSize 20 query matchQuery("text", "life")
          )
      }.await.result
      val fragments = resp.hits.hits.head.highlightFragments("text")
      fragments.size shouldBe 1
      fragments.head.trim shouldBe "worlds, to seek out new <em>life</em>"
    }
  }
}
