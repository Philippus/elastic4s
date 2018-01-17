//package com.sksamuel.elastic4s.search.suggestions
//
//import com.sksamuel.elastic4s.{ElasticDsl, Indexable}
//import com.sksamuel.elastic4s.searches.suggestion.Fuzziness
//import com.sksamuel.elastic4s.testkit.{DiscoveryLocalNodeProvider, ElasticSugar}
//import org.scalatest.{Matchers, WordSpec}
//
//import scala.util.Try
//
//class CompletionSuggestionsTest extends WordSpec with Matchers with ElasticSugar with DiscoveryLocalNodeProvider {
//
//  implicit object SongIndexable extends Indexable[Song] {
//    override def json(t: Song): String = s"""{"name":"${t.name}", "artist":"${t.artist}"}"""
//  }
//
//  private val Index = "complsuggest"
//  private val indexType = Index / "music"
//
//  Try {
//    client.execute {
//      ElasticDsl.deleteIndex(Index)
//    }.await
//  }
//
//  client.execute {
//    createIndex(Index).mappings(
//      mapping("music").fields(
//        completionField("name")
//      )
//    )
//  }.await
//
//  client.execute(
//    bulk(
//      indexInto(indexType) doc Song("Rocket Man", "Kate Bush"),
//      indexInto(indexType) doc Song("Rubberband Girl", "Kate Bush"),
//      indexInto(indexType) doc Song("Running Up that Hill", "Kate Bush"),
//      indexInto(indexType) doc Song("The Fog", "Kate Bush"),
//      indexInto(indexType) doc Song("The Red Shoes", "Kate Bush"),
//      indexInto(indexType) doc Song("The Dreaming", "Kate Bush"),
//      indexInto(indexType) doc Song("The Big Sky", "Kate Bush")
//    )
//  ).await
//
//  blockUntilCount(7, Index)
//
//  private val resp = client.execute {
//    search(indexType).suggestions {
//      completionSuggestion("a").on("name").prefix("Ru")
//    }
//  }.await
//
//  private val result = resp.suggestion("a")
//  println(result)
//
//  private val entries = result.entries.toList
//  println(entries)
//
//  "completion suggestions" should {
//    "support lookups by text" in {
//
//      val resp = client.execute {
//        search(indexType).suggestions {
//          completionSuggestion("a").on("name").text("The B")
//        }
//      }.await
//
//      val entry = resp.suggestion("a").entries.head
//      entry.optionsText shouldBe List("The Big Sky")
//    }
//    "support max results" in {
//
//      val resp = client.execute {
//        search(indexType).suggestions {
//          completionSuggestion("a").on("name").prefix("r").size(1)
//        }
//      }.await
//
//      val entry = resp.suggestion("a").entries.head
//      entry.optionsText shouldBe List("Rocket Man")
//    }
//    "support lookups by prefix" in {
//
//      val resp = client.execute {
//        search(indexType).suggestions {
//          completionSuggestion("a").on("name").prefix("ru")
//        }
//      }.await
//
//      val entry = resp.suggestion("a").entries.head
//      entry.optionsText shouldBe List("Rubberband Girl", "Running Up that Hill")
//    }
//    "support fuzzy prefix lookups" in {
//
//      val resp = client.execute {
//        search(indexType).suggestions {
//          completionSuggestion("a").on("name").prefix("Rabber", Fuzziness.One)
//        }
//      }.await
//
//      val entry = resp.suggestion("a").entries.head
//      entry.optionsText shouldBe List("Rubberband Girl")
//    }
//  }
//}
