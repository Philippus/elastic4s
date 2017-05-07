package com.sksamuel.elastic4s.search.suggestions

import com.sksamuel.elastic4s.{ElasticsearchClientUri, Indexable}
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder.SuggestMode
import org.scalatest.{Matchers, WordSpec}

class TermSuggestionsTest extends WordSpec with Matchers with ElasticSugar with ElasticDsl {

  implicit object SongIndexable extends Indexable[Song] {
    override def json(t: Song): String = s"""{"name":"${t.name}", "artist":"${t.artist}"}"""
  }

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  private val Index = "termsuggest"
  private val indexType = Index / "music"

  http.execute(
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
      indexInto(indexType) doc Song("Monster", "Mumford and sons"),
      indexInto(indexType) doc Song("Goodbye the yellow brick road", "Elton John"),
      indexInto(indexType) doc Song("Your song", "Elton John")
    ).refresh(RefreshPolicy.IMMEDIATE)
  ).await

  "suggestions" should {
    "support results lookup by name" in {

      val resp = http.execute {
        search(indexType).suggestions {
          termSuggestion("a").on("artist").text("taylor swuft")
        }
      }.await

      resp.termSuggestion("a")("taylor").options.isEmpty shouldBe true
      resp.termSuggestion("a")("swuft").optionsText shouldBe Seq("swift")
    }
    "bring back suggestions for matching terms when mode is always" in {

      val suggestionA = termSuggestion("a").on("artist") text "Razzle Kacks" mode SuggestMode.ALWAYS
      val resp = http.execute {
        search(indexType).suggestions(suggestionA)
      }.await

      resp.termSuggestion("a")("razzle").optionsText shouldBe Seq("rizzle")
      resp.termSuggestion("a")("kacks").optionsText shouldBe Seq("kicks")
    }
    "bring back suggestions that are more popular when popular mode is set" in {

      val resp = http.execute {
        search(indexType).suggestions {
          termSuggestion("a", "artist", "Quoon") mode SuggestMode.POPULAR
        }
      }.await
      resp.termSuggestion("a")("quoon").optionsText shouldBe Seq("queen")

    }
    "allow us to set the max edits to be counted as a suitable suggestion" in {

      val resp = http.execute {
        search(indexType).suggestions {
          termSuggestion("a") on "artist" text "Quean" maxEdits 1 // so Quean->Queen but not Quean -> Quoon
        }
      }.await
      resp.termSuggestion("a")("quean").optionsText shouldBe Seq("queen")
    }
    "allow us to set min word length to be suggested for" in {
      val resp = http.execute {
        search(indexType).suggestions {
          termSuggestion("a", "artist", "joan") minWordLength 5
        }
      }.await
      // we set min word to 5 so the only possible suggestion of John should not be included
      resp.termSuggestion("a")("joan").options.size shouldBe 0
    }
  }
}

case class Song(name: String, artist: String)
