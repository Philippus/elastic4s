package com.sksamuel.elastic4s.search.highlight

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import com.sksamuel.elastic4s.testkit.{DualClient, DualElasticSugar}
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{Matchers, WordSpec}

class HighlightTest extends WordSpec with Matchers with ElasticDsl with DualElasticSugar with DualClient {

  override protected def beforeRunTests() = {
    execute {
      createIndex("intros").mappings(
        mapping("tv").fields(
          textField("name").stored(true),
          textField("text").stored(true)
        )
      )
    }.await

    execute {
      indexInto("intros/tv")
        .fields(
          "name" -> "star trek",
          "text" -> "Space, the final frontier. These are the voyages of the starship Enterprise. Its continuing mission: to explore strange new worlds, to seek out new life and new civilisations, to boldly go where no one has gone before."
        ).refresh(RefreshPolicy.IMMEDIATE)
    }.await
  }

  "highlighting" should {
    "highlight selected words" in {

      val resp = execute {
        search("intros").matchQuery("text", "frontier").highlighting(
          highlight("text")
        )
      }.await

      resp.size shouldBe 1

      val fragments = resp.hits.hits.head.highlightFragments("text")
      fragments.size shouldBe 1
      fragments.head shouldBe
        "Space, the final <em>frontier</em>. These are the voyages of the starship Enterprise. Its continuing mission"
    }
    "use fragment size" in {
      val resp = execute {
        search("intros") query "new" highlighting (
          highlight("text").requireFieldMatch(false) fragmentSize 15
          )
      }.await
      val fragments = resp.hits.hits.head.highlightFragments("text")
      fragments.size shouldBe 3
      fragments.head shouldBe " <em>new</em> worlds, to"
      fragments(1) shouldBe " seek out <em>new</em>"
      fragments(2) shouldBe " life and <em>new</em>"
    }
    "use number of fragments size" in {
      val resp = execute {
        search("intros") query "text:new" highlighting (
          highlight("text") fragmentSize 5 numberOfFragments 2
          )
      }.await
      val fragments = resp.hits.hits.head.highlightFragments("text")
      fragments.size shouldBe 2
    }
    "use no match size" in {
      val resp = execute {
        search("intros") query "trek" highlighting (
          highlight("text") noMatchSize 50
          )
      }.await
      val fragments = resp.hits.hits.head.highlightFragments("text")
      fragments.size shouldBe 1
      fragments.head shouldBe "Space, the final frontier. These are the voyages"
    }
    "use pre tags" in {
      val resp = execute {
        search("intros") query matchQuery("text", "frontier") highlighting (
          highlight("text") fragmentSize 20 preTag "<picard>"
          )
      }.await
      val fragments = resp.hits.hits.head.highlightFragments("text")
      fragments.size shouldBe 1
      fragments.head.trim shouldBe "<picard>frontier</em>. These are"
    }
    "use post tags" in {
      val resp = execute {
        search("intros" / "tv") query matchQuery("text", "frontier") highlighting (
          highlight("text") fragmentSize 20 preTag "<riker>"
          )
      }.await
      val fragments = resp.hits.hits.head.highlightFragments("text")
      fragments.size shouldBe 1
      fragments.head.trim shouldBe "<riker>frontier</em>. These are"
    }
    "use highlight query" in {
      val resp = execute {
        search("intros" / "tv") query matchQuery("text", "frontier") highlighting (
          highlight("text") fragmentSize 20 query matchQuery("text", "life")
          )
      }.await
      val fragments = resp.hits.hits.head.highlightFragments("text")
      fragments.size shouldBe 1
      fragments.head.trim shouldBe "out new <em>life</em> and"
    }
  }
}
