//package com.sksamuel.elastic4s.search
//
//import com.sksamuel.elastic4s.ElasticDsl._
//import com.sksamuel.elastic4s.searches
//import com.sksamuel.elastic4s.testkit.ElasticSugar
//import org.scalatest.{Matchers, WordSpec}
//
//class HighlightTest extends WordSpec with ElasticSugar with Matchers {
//
//  client.execute {
//    index into "intros/tv" fields("name" -> "star trek", "text" -> "Space, the final frontier. These are the voyages of the starship Enterprise. Its continuing mission: to explore strange new worlds, to seek out new life and new civilisations, to boldly go where no one has gone before.")
//  }.await
//
//  refresh("intros")
//  blockUntilCount(1, "intros")
//
//  // todo figure out why this is all broken
//  "highlighting" should {
//    "highlight selected words" ignore {
//      val resp = client.execute {
//        search in "intros" / "tv" query "frontier" highlighting highlight("text")
//      }.await
//      val fragments = resp.hits.head.highlightFields("text").fragments()
//      fragments.size shouldBe 1
//      fragments
//        .head
//        .string() shouldBe "Space, the final <em>frontier</em>. These are the voyages of the starship Enterprise. Its continuing mission"
//    }
//    "use fragment size" ignore {
//      val resp = client.execute {
//        search in "intros" / "tv" query "new" highlighting (
//          highlight("text") fragmentSize 15
//          )
//      }.await
//      val fragments = resp.hits.head.highlightFields("text").fragments()
//      fragments.size shouldBe 3
//      fragments(0).string() shouldBe " <em>new</em> worlds, to"
//      fragments(1).string() shouldBe " seek out <em>new</em>"
//      fragments(2).string() shouldBe " life and <em>new</em>"
//    }
//    "use number of fragments size" ignore {
//      val resp = client.execute {
//        search in "intros" / "tv" query "new" highlighting (
//          highlight("text") fragmentSize 5 numberOfFragments 2
//          )
//      }.await
//      val fragments = resp.hits.head.highlightFields("text").fragments()
//      fragments.size shouldBe 2
//    }
//    "use no match size" ignore {
//      val resp = client.execute {
//        search in "intros" / "tv" query "trek" highlighting (
//          highlight("text") noMatchSize 50
//          )
//      }.await
//      val fragments = resp.hits.head.highlightFields("text").fragments()
//      fragments.size shouldBe 1
//      fragments(0).string() shouldBe "Space, the final frontier. These are the voyages"
//    }
//    "use pre tags" ignore {
//      val resp = client.execute {
//        search in "intros" / "tv" query "frontier" highlighting (
//          highlight("text") fragmentSize 20 preTag "<picard>"
//          )
//      }.await
//      val fragments = resp.hits.head.highlightFields("text").fragments()
//      fragments.size shouldBe 1
//      fragments(0).string.trim shouldBe "<picard>frontier</em>. These are"
//    }
//    "use post tags" ignore {
//      val resp = client.execute {
//        search in "intros" / "tv" query "frontier" highlighting (
//          highlight("text") fragmentSize 20 preTag "<riker>"
//          )
//      }.await
//      val fragments = resp.hits.head.highlightFields("text").fragments()
//      fragments.size shouldBe 1
//      fragments(0).string.trim shouldBe "<riker>frontier</em>. These are"
//    }
//    "use highlight query" ignore {
//      val resp = client.execute {
//        search in "intros" / "tv" query "frontier" highlighting (
//          highlight("text") fragmentSize 20 query "life"
//          )
//      }.await
//      val fragments = resp.hits.head.highlightFields("text").fragments()
//      fragments.size shouldBe 1
//      fragments(0).string.trim shouldBe "out new <em>life</em> and"
//    }
//  }
//}
