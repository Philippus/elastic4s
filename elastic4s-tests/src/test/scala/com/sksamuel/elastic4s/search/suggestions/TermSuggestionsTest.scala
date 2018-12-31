package com.sksamuel.elastic4s.search.suggestions

import com.sksamuel.elastic4s.requests.searches.suggestion.SuggestMode
import com.sksamuel.elastic4s.testkit.DockerTests
import com.sksamuel.elastic4s.Indexable
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class TermSuggestionsTest extends WordSpec with Matchers with DockerTests {

  implicit object SongIndexable extends Indexable[Song] {
    override def json(t: Song): String = s"""{"name":"${t.name}", "artist":"${t.artist}"}"""
  }

  private val Index = "termsuggest"
  private val indexType = Index / "music"

  Try {
    client.execute {
      deleteIndex(Index)
    }.await
  }

  client.execute {
    createIndex(Index)
  }.await

  client.execute(
    bulk(
      indexInto(indexType) doc Song("style", "taylor swift"),
      indexInto(indexType) doc Song("shake it off", "Taylor Swift"),
      indexInto(indexType) doc Song("a new england", "kirsty maccoll"),
      indexInto(indexType) doc Song("blank page", "taylor swift"),
      indexInto(indexType) doc Song("I want it all", "queen"),
      indexInto(indexType) doc Song("I to break free", "queen"),
      indexInto(indexType) doc Song("radio gaga", "queen"),
      indexInto(indexType) doc Song("we are the champions", "quoon"),
      indexInto(indexType) doc Song("Down with the trumpets", "rizzle kicks"),
      indexInto(indexType) doc Song("Down with the trombones", "razzle kacks"),
      indexInto(indexType) doc Song("lover of the light", "Mumford and sons"),
      indexInto(indexType) doc Song("Monster", "Mumford and sons"),
      indexInto(indexType) doc Song("Goodbye the yellow brick road", "Elton John"),
      indexInto(indexType) doc Song("Your song", "Elton John")
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "suggestions" should {
    "support results lookup by name" in {

      val resp = client.execute {
        search(indexType).suggestions {
          termSuggestion("a").on("artist").text("taylor swuft")
        }
      }.await.result

      resp.termSuggestion("a")("taylor").options.isEmpty shouldBe true
      resp.termSuggestion("a")("swuft").optionsText shouldBe Seq("swift")
    }
    "bring back suggestions for matching terms when mode is always" in {

      val resp = client.execute {
        search(indexType).suggestions(termSuggestion("a", "artist", "razzle kacks").mode(SuggestMode.ALWAYS))
      }.await.result

      resp.termSuggestion("a")("razzle").optionsText shouldBe Seq("rizzle")
      resp.termSuggestion("a")("kacks").optionsText shouldBe Seq("kicks")
    }
    // seems to be broken in es 7 alpha 2
    "bring back suggestions that are more popular when popular mode is set" in {

      val resp = client.execute {
        search(indexType).suggestions {
          termSuggestion("a", "artist", "quoon") mode SuggestMode.ALWAYS
        }
      }.await.result
      resp.termSuggestion("a")("quoon").optionsText shouldBe Seq("queen")

    }
    "allow us to set the max edits to be counted as a suitable suggestion" in {

      val resp = client.execute {
        search(indexType).suggestions {
          termSuggestion("a", "artist" , "Quean").maxEdits(1) // so Quean->Queen but not Quean -> Quoon
        }
      }.await.result
      resp.termSuggestion("a")("quean").optionsText shouldBe Seq("queen")
    }
    "allow us to set min word length to be suggested for" in {
      val resp = client.execute {
        search(indexType).suggestions {
          termSuggestion("a", "artist", "joan") minWordLength 5
        }
      }.await.result
      // we set min word to 5 so the only possible suggestion of John should not be included
      resp.termSuggestion("a")("joan").options.size shouldBe 0
    }
  }
}

case class Song(name: String, artist: String)
