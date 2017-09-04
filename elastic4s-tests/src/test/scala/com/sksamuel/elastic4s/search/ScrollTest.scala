package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.{DiscoveryLocalNodeProvider, DualClientTests}
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._
import scala.util.Try

class ScrollTest extends WordSpec with Matchers with ElasticDsl with DiscoveryLocalNodeProvider {

  Try {
    http.execute {
      deleteIndex("katebush")
    }.await
  }

  http.execute {
    createIndex("katebush").mappings(
      mapping("songs").fields(
        intField("year"),
        textField("name").fielddata(true).stored(true)
      )
    )
  }.await

  http.execute {
    bulk(
      indexInto("katebush/songs").fields("name" -> "hounds of love", "year" -> "1985").id("1"),
      indexInto("katebush/songs").fields("name" -> "top of the city", "year" -> "1985").id("2"),
      indexInto("katebush/songs").fields("name" -> "wuthering heights", "year" -> "1979").id("3"),
      indexInto("katebush/songs").fields("name" -> "dream of sheep", "year" -> "1985").id("4"),
      indexInto("katebush/songs").fields("name" -> "waking the watch", "year" -> "1985").id("5"),
      indexInto("katebush/songs").fields("name" -> "watching you watching me", "year" -> "1985").id("6"),
      indexInto("katebush/songs").fields("name" -> "cloudbusting", "year" -> "1985").id("7"),
      indexInto("katebush/songs").fields("name" -> "under ice", "year" -> "1985").id("8"),
      indexInto("katebush/songs").fields("name" -> "jig of life", "year" -> "1985").id("9"),
      indexInto("katebush/songs").fields("name" -> "hello earth", "year" -> "1985").id("0")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "a scroll" should {
    "return all results" in {

      val resp1 = http.execute {
        search("katebush" / "songs")
          .query("1985")
          .scroll("1m")
          .limit(2)
          .sortBy(fieldSort("name"))
          .storedFields("name")
      }.await
      resp1.hits.hits.map(_.storedField("name").value).toList shouldBe List("top of the city", "cloudbusting")

      val resp2 = http.execute {
        searchScroll(resp1.scrollId.get).keepAlive("1m")
      }.await
      resp2.hits.hits.map(_.storedField("name").value).toList shouldBe List("dream of sheep", "hello earth")

      val resp3 = http.execute {
        searchScroll(resp2.scrollId.get).keepAlive("1m")
      }.await
      resp3.hits.hits.map(_.storedField("name").value).toList shouldBe List("hounds of love", "under ice")

      val resp4 = http.execute {
        searchScroll(resp3.scrollId.get).keepAlive("1m")
      }.await
      resp4.hits.hits.map(_.storedField("name").value).toList shouldBe List("jig of life", "watching you watching me")

      val resp5 = http.execute {
        searchScroll(resp4.scrollId.get)
      }.await
      resp5.hits.hits.map(_.storedField("name").value).toList shouldBe List("waking the watch")
    }
  }

  "a 'searchScroll.keepAlive'" should {
    "not interpret FiniteDuration as 'id'" in {
      val resp1 = http.execute {
        search("katebush" / "songs")
          .query("1985")
          .scroll("1m")
          .limit(2)
          .sortBy(fieldSort("name"))
          .storedFields("name")
      }.await

      val resp2 = http.execute {
        searchScroll(resp1.scrollId.get).keepAlive(1.minute)
      }.await
      resp2.hits.hits.map(_.storedField("name").value).toList shouldBe List("dream of sheep", "hello earth")
    }
  }

  "a 'searchScroll.slice'" should {
    "return sliced results" in {
      val resp1 = http.execute {
        search("katebush" / "songs")
          .slice(0, 2)
          .query("1985")
          .scroll("1m")
          .limit(4)
          .storedFields("name")
      }.await

      val resp2 = http.execute {
        searchScroll(resp1.scrollId.get).keepAlive(1.minute)
      }.await

      val resp3 = http.execute {
        search("katebush" / "songs")
          .slice(1, 2)
          .query("1985")
          .scroll("1m")
          .limit(4)
          .storedFields("name")
      }.await

      val resp4 = http.execute {
        searchScroll(resp3.scrollId.get).keepAlive(1.minute)
      }.await

      (resp1.hits.hits.length + resp2.hits.hits.length) shouldBe 4
      (resp3.hits.hits.length + resp4.hits.hits.length) shouldBe 5
      val merged = Seq(resp1, resp2, resp3, resp4).flatMap(resp => resp.hits.hits.map(_.storedField("name").value.asInstanceOf[String])).toList.distinct
      merged.length shouldBe 9
      merged.max shouldBe "watching you watching me"
      merged.min shouldBe "cloudbusting"
    }
  }

  "a clearScroll" should {
    "clear scrolls" in {

      val searchDefinition = search("katebush" / "songs")
        .query("1985")
        .scroll("1m")
        .size(2)
        .sortBy(fieldSort("name"))
        .storedFields("name")

      val resp1 = http.execute {
        searchDefinition
      }.await

      val resp2 = http.execute {
        searchDefinition
      }.await

      val resp = http.execute {
        clearScroll(resp1.scrollId.get, resp2.scrollId.get)
      }.await

      resp.succeeded should be(true)
      resp.num_freed should be > 0
    }
  }
}
