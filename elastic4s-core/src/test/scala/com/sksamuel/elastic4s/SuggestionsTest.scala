package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.source.Indexable
import org.scalatest.{WordSpec, Matchers}

class SuggestionsTest extends WordSpec with Matchers with ElasticSugar {

  implicit object SongIndexable extends Indexable[Song] {
    override def json(t: Song): String = s"""{"name":"${t.name}", "artist":"${t.artist}"}"""
  }

  import ElasticDsl._

  private val indexType = "suggestionstest" / "music"

  client.execute(
    bulk(
      index into indexType source Song("style", "taylor swift"),
      index into indexType source Song("shake it off", "Taylor Swift"),
      index into indexType source Song("a new england", "kirsty maccoll"),
      index into indexType source Song("blank page", "taylor swift"),
      index into indexType source Song("I want it all", "Queen"),
      index into indexType source Song("I to break free", "Queen"),
      index into indexType source Song("radio gaga", "Queen"),
      index into indexType source Song("we are the champions", "Quoon"),
      index into indexType source Song("Down with the trumpets", "Rizzle Kicks"),
      index into indexType source Song("Down with the trombones", "Razzle Kacks"),
      index into indexType source Song("lover of the light", "Mumford and sons"),
      index into indexType source Song("Monster", "Mumford and sons")
    )
  ).await

  blockUntilCount(8, "suggestionstest")

  "suggestions" should {
    "bring back results" in {
      val resp = client.execute {
        search in indexType suggestions {
          suggest as "a" field "artist" on "taylor swaft"
        }
      }.await
      resp.suggestion("a").get.entry("taylor").options.isEmpty shouldBe true
      resp.suggestion("a").get.entry("swaft").optionsText shouldBe Array("swift")
    }
    "bring back suggestions for matching terms when mode is always" in {
      val resp = client.execute {
        search in indexType suggestions {
          suggest as "a" field "artist" on "Razzle Kacks" mode SuggestMode.Always
        }
      }.await
      resp.suggestion("a").get.entry("razzle").optionsText shouldBe Array("rizzle")
      resp.suggestion("a").get.entry("kacks").optionsText shouldBe Array("kicks")
    }
    "bring back suggestions that are more popular when popular mode is set" in {
      val resp = client.execute {
        search in indexType suggestions {
          suggest as "a" field "artist" on "Quoon" mode SuggestMode.Popular
        }
      }.await
      resp.suggestion("a").get.entry("quoon").optionsText shouldBe Array("queen")
    }
    "allow us to set the max edits to be counted as a suitable suggestion" in {
      val resp = client.execute {
        search in indexType suggestions {
          suggest as "a" field "artist" on "Quean" maxEdits 1 // so Quean->Queen but not Quean -> Quoon
        }
      }.await
      resp.suggestion("a").get.entry("quean").optionsText shouldBe Array("queen")
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
