package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.Indexable
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder.SuggestMode
import org.scalatest.{Matchers, WordSpec}

class SuggestionsTest extends WordSpec with Matchers with ElasticSugar {

  implicit object SongIndexable extends Indexable[Song] {
    override def json(t: Song): String = s"""{"name":"${t.name}", "artist":"${t.artist}"}"""
  }

  private val indexType = "suggestionstest" / "music"

  client.execute(
    bulk(
      indexInto(indexType) doc Song("style", "taylor swift"),
      indexInto(indexType) doc Song("shake it off", "Taylor Swift"),
      indexInto(indexType) doc Song("a new england", "kirsty maccoll"),
      indexInto(indexType) doc Song("blank page", "taylor swift"),
      indexInto(indexType) doc Song("I want it all", "Queen"),
      indexInto(indexType) doc Song("I to break free", "Queen"),
      indexInto(indexType) doc Song("radio gaga", "Queen"),
      indexInto(indexType) doc Song("we are the champions", "Quoon"),
      indexInto(indexType) doc Song("Down with the trumpets", "Rizzle Kicks"),
      indexInto(indexType) doc Song("Down with the trombones", "Razzle Kacks"),
      indexInto(indexType) doc Song("lover of the light", "Mumford and sons"),
      indexInto(indexType) doc Song("Monster", "Mumford and sons")
    )
  ).await

  blockUntilCount(8, "suggestionstest")

  "suggestions" should {
    "support results lookup by name" in {

      val resp = client.execute {
        search(indexType).suggestions {
          termSuggestion("a").on("artist").text("taylor swuft")
        }
      }.await

      resp.suggestion("artistsugg").entry("taylor").options.isEmpty shouldBe true
      resp.termSuggestion("artistsugg").entry("swaft").optionsText shouldBe Seq("swift")
    }
    "bring back suggestions for matching terms when mode is always" in {

      val suggestionA = termSuggestion("a").on("artist") text "Razzle Kacks" mode SuggestMode.ALWAYS
      val resp = client.execute {
        search(indexType).suggestions(suggestionA)
      }.await

      resp.suggestion("a").entry("razzle").optionsText shouldBe Array("rizzle")
      resp.suggestion("a").entry("kacks").optionsText shouldBe Array("kicks")
    }
    "bring back suggestions that are more popular when popular mode is set" in {

      val resp = client.execute {
        search(indexType).suggestions {
          termSuggestion("a") on "artist" text "Quoon" mode SuggestMode.POPULAR
        }
      }.await
      resp.suggestion("a").entry("quoon").optionsText shouldBe Array("queen")

    }
    "allow us to set the max edits to be counted as a suitable suggestion" in {

      val resp = client.execute {
        search(indexType).suggestions {
          termSuggestion("a") on "artist" text "Quean" maxEdits 1 // so Quean->Queen but not Quean -> Quoon
        }
      }.await
      resp.suggestion("a").entry("quean").optionsText shouldBe Array("queen")
    }
    //    "allow us to set min word length to be suggested for" in {
    //      val resp = client.execute {
    //        search in indexType suggestions {
    //          suggest as "a" field "artist" on "Mamford ind sans"  minWordLength 2
    //        }
    //      }.await
    //      resp.suggestion("a").get.entryTerms shouldBe Array("mamford", "sans")
    //      resp.suggestion("a").get.entry("mamford").optionsText shouldBe Array("mumford")
    //      resp.suggestion("a").get.entry("sons").optionsText shouldBe Array("sans")
    //    }
  }
}

case class Song(name: String, artist: String)
