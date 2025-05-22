package com.sksamuel.elastic4s.search.suggestions

import com.sksamuel.elastic4s.Indexable
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.suggestion.SuggestMode
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Try

class TermSuggestionsTest extends AnyWordSpec with Matchers with DockerTests {

  implicit object SongIndexable extends Indexable[Song] {
    override def json(t: Song): String = s"""{"name":"${t.name}", "artist":"${t.artist}"}"""
  }

  private val Index = "termsuggest"
  private val index = Index

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
      indexInto(index) doc Song("style", "taylor swift"),
      indexInto(index) doc Song("shake it off", "Taylor Swift"),
      indexInto(index) doc Song("a new england", "kirsty maccoll"),
      indexInto(index) doc Song("blank page", "taylor swift"),
      indexInto(index) doc Song("I want it all", "queen"),
      indexInto(index) doc Song("I to break free", "queen"),
      indexInto(index) doc Song("radio gaga", "queen"),
      indexInto(index) doc Song("we are the champions", "quoon"),
      indexInto(index) doc Song("Down with the trumpets", "rizzle kicks"),
      indexInto(index) doc Song("Down with the trombones", "razzle kacks"),
      indexInto(index) doc Song("lover of the light", "Mumford and sons"),
      indexInto(index) doc Song("Monster", "Mumford and sons"),
      indexInto(index) doc Song("Goodbye the yellow brick road", "Elton John"),
      indexInto(index) doc Song("Your song", "Elton John")
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "suggestions" should {
    "support results lookup by name" in {

      val resp = client.execute {
        search(index).suggestions {
          termSuggestion("a", "artist", "taylor swuft")
        }
      }.await.result

      resp.termSuggestion("a")("taylor").options.isEmpty shouldBe true
      resp.termSuggestion("a")("swuft").optionsText shouldBe Seq("swift")
    }
    "bring back suggestions for matching terms when mode is always" in {

      val resp = client.execute {
        search(index).suggestions(termSuggestion("a", "artist", "razzle kacks").mode(SuggestMode.ALWAYS))
      }.await.result

      resp.termSuggestion("a")("razzle").optionsText shouldBe Seq("rizzle")
      resp.termSuggestion("a")("kacks").optionsText shouldBe Seq("kicks")
    }
    // seems to be broken in es 7 alpha 2
    "bring back suggestions that are more popular when popular mode is set" in {

      val resp = client.execute {
        search(index).suggestions {
          termSuggestion("a", "artist", "quoon") mode SuggestMode.ALWAYS
        }
      }.await.result
      resp.termSuggestion("a")("quoon").optionsText shouldBe Seq("queen")

    }
    "allow us to set the max edits to be counted as a suitable suggestion" in {

      val resp = client.execute {
        search(index).suggestions {
          termSuggestion("a", "artist", "Quean").maxEdits(1) // so Quean->Queen but not Quean -> Quoon
        }
      }.await.result
      resp.termSuggestion("a")("quean").optionsText shouldBe Seq("queen")
    }
    "allow us to set min word length to be suggested for" in {
      val resp = client.execute {
        search(index).suggestions {
          termSuggestion("a", "artist", "joan") minWordLength 5
        }
      }.await.result
      // we set min word to 5 so the only possible suggestion of John should not be included
      resp.termSuggestion("a")("joan").options.size shouldBe 0
    }
  }
}

case class Song(name: String, artist: String)
