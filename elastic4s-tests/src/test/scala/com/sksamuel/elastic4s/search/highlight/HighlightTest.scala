package com.sksamuel.elastic4s.search.highlight

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class HighlightTest extends WordSpec with Matchers with ElasticDsl with DiscoveryLocalNodeProvider {

  Try {
    http.execute {
      deleteIndex("intros")
    }.await
  }

  http.execute {
    createIndex("intros").mappings(
      mapping("tv").fields(
        textField("name").stored(true),
        textField("text").stored(true)
      )
    )
  }.await

  http.execute {
    indexInto("intros/tv")
      .fields(
        "name" -> "star trek",
        "text" -> "Space, the final frontier. These are the voyages of the starship Enterprise. Its continuing mission: to explore strange new worlds, to seek out new life and new civilisations, to boldly go where no one has gone before."
      ).refresh(RefreshPolicy.Immediate)
  }.await

  "highlighting" should {
    "highlight selected words" in {

      val resp = http.execute {
        search("intros").matchQuery("text", "frontier").highlighting(
          highlight("text")
        )
      }.await

      resp.size shouldBe 1

      val fragments = resp.hits.hits.head.highlightFragments("text")
      fragments.size shouldBe 1
      fragments.head shouldBe
        "Space, the final <em>frontier</em>."
    }
    "use fragment size" in {
      val resp = http.execute {
        search("intros") query "new" highlighting (
          highlight("text").requireFieldMatch(false) fragmentSize 15
          )
      }.await
      val fragments = resp.hits.hits.head.highlightFragments("text")
      fragments.size shouldBe 3
      fragments.head shouldBe "explore strange <em>new</em>"
      fragments(1) shouldBe "worlds, to seek out <em>new</em>"
      fragments(2) shouldBe "life and <em>new</em> civilisations"
    }
    "use number of fragments size" in {
      val resp = http.execute {
        search("intros") query "text:new" highlighting (
          highlight("text") fragmentSize 5 numberOfFragments 2
          )
      }.await
      val fragments = resp.hits.hits.head.highlightFragments("text")
      fragments.size shouldBe 2
    }
    "use no match size" in {
      val resp = http.execute {
        search("intros") query "trek" highlighting (
          highlight("text") noMatchSize 50
          )
      }.await
      val fragments = resp.hits.hits.head.highlightFragments("text")
      fragments.size shouldBe 1
      fragments.head shouldBe "Space, the final frontier. These are the voyages of"
    }
    "use pre tags" in {
      val resp = http.execute {
        search("intros") query matchQuery("text", "frontier") highlighting (
          highlight("text") fragmentSize 20 preTag "<picard>"
          )
      }.await
      val fragments = resp.hits.hits.head.highlightFragments("text")
      fragments.size shouldBe 1
      fragments.head.trim shouldBe "Space, the final <picard>frontier</em>"
    }
    "use post tags" in {
      val resp = http.execute {
        search("intros" / "tv") query matchQuery("text", "frontier") highlighting (
          highlight("text") fragmentSize 20 postTag "<riker>"
          )
      }.await
      val fragments = resp.hits.hits.head.highlightFragments("text")
      fragments.size shouldBe 1
      fragments.head.trim shouldBe "Space, the final <em>frontier<riker>"
    }
    "use highlight query" in {
      val resp = http.execute {
        search("intros" / "tv") query matchQuery("text", "frontier") highlighting (
          highlight("text") fragmentSize 20 query matchQuery("text", "life")
          )
      }.await
      val fragments = resp.hits.hits.head.highlightFragments("text")
      fragments.size shouldBe 1
      fragments.head.trim shouldBe "worlds, to seek out new <em>life</em>"
    }
  }
}
