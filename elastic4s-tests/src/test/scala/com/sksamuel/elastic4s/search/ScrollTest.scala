package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._
import scala.util.Try

class ScrollTest extends WordSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("katebush")
    }.await
  }

  client.execute {
    createIndex("katebush").mappings(
      mapping("songs").fields(
        intField("year"),
        textField("name").fielddata(true).stored(true)
      )
    )
  }.await

  client.execute {
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

      val resp1 = client.execute {
        search("katebush")
          .query("1985")
          .scroll("1m")
          .limit(2)
          .sortBy(fieldSort("name"))
          .storedFields("name")
      }.await.result
      resp1.hits.hits.map(_.storedField("name").value).toList shouldBe List("top of the city", "cloudbusting")

      val resp2 = client.execute {
        searchScroll(resp1.scrollId.get).keepAlive("1m")
      }.await
      resp2.result.hits.hits.map(_.storedField("name").value).toList shouldBe List("dream of sheep", "hello earth")

      val resp3 = client.execute {
        searchScroll(resp2.result.scrollId.get).keepAlive("1m")
      }.await
      resp3.result.hits.hits.map(_.storedField("name").value).toList shouldBe List("hounds of love", "under ice")

      val resp4 = client.execute {
        searchScroll(resp3.result.scrollId.get).keepAlive("1m")
      }.await
      resp4.result.hits.hits.map(_.storedField("name").value).toList shouldBe List("jig of life", "watching you watching me")

      val resp5 = client.execute {
        searchScroll(resp4.result.scrollId.get)
      }.await
      resp5.result.hits.hits.map(_.storedField("name").value).toList shouldBe List("waking the watch")
    }
    "return an error if the scroll id doesn't parse" in {
      val resp = client.execute {
        searchScroll("wibble").keepAlive("1m")
      }.await
      resp.error.`type` shouldBe "illegal_argument_exception"
    }
    "return an error if the scroll doesn't exist" in {
      val resp = client.execute {
        searchScroll("DXF1ZXJ5QW5kRmV0Y2gBAAAAAAAAAD4WYm9laVYtZndUQlNsdDcwakFMNjU1QQ==").keepAlive("1m")
      }.await
      resp.error.`type` shouldBe "search_phase_execution_exception"
    }
  }

  "a 'searchScroll.keepAlive'" should {
    "not interpret FiniteDuration as 'id'" in {
      val resp1 = client.execute {
        search("katebush")
          .query("1985")
          .scroll("1m")
          .limit(2)
          .sortBy(fieldSort("name"))
          .storedFields("name")
      }.await.result

      val resp2 = client.execute {
        searchScroll(resp1.scrollId.get).keepAlive(1.minute)
      }.await
      resp2.result.hits.hits.map(_.storedField("name").value).toList shouldBe List("dream of sheep", "hello earth")
    }
  }

  "a 'searchScroll.slice'" should {
    "return sliced results" in {
      val resp1 = client.execute {
        search("katebush")
          .slice(0, 2)
          .query("1985")
          .scroll("1m")
          .limit(4)
          .storedFields("name")
      }.await.result

      val resp2 = client.execute {
        searchScroll(resp1.scrollId.get).keepAlive(1.minute)
      }.await.result

      val resp3 = client.execute {
        search("katebush")
          .slice(1, 2)
          .query("1985")
          .scroll("1m")
          .limit(4)
          .storedFields("name")
      }.await.result

      val resp4 = client.execute {
        searchScroll(resp3.scrollId.get).keepAlive(1.minute)
      }.await.result

      (resp1.hits.hits ++ resp2.hits.hits ++ resp3.hits.hits ++ resp4.hits.hits).length shouldBe 9
      val merged = Seq(resp1.hits.hits, resp3.hits.hits, resp2.hits.hits, resp4.hits.hits).flatMap(resp => resp.map(_.storedField("name").value.asInstanceOf[String])).toList.distinct
      merged.length shouldBe 9
      merged.max shouldBe "watching you watching me"
      merged.min shouldBe "cloudbusting"
    }
  }

  "a clearScroll" should {
    "clear scrolls" in {

      val searchDefinition = search("katebush")
        .query("1985")
        .scroll("1m")
        .size(2)
        .sortBy(fieldSort("name"))
        .storedFields("name")

      val resp1 = client.execute {
        searchDefinition
      }.await.result

      val resp2 = client.execute {
        searchDefinition
      }.await.result

      val resp = client.execute {
        clearScroll(resp1.scrollId.get, resp2.scrollId.get)
      }.await

      resp.result.succeeded should be(true)
      resp.result.num_freed should be > 0
    }
    "return an error if the scroll id doesn't parse" in {
      client.execute {
        clearScroll("wibble")
      }.await.error.`type` shouldBe "illegal_argument_exception"
    }
  }
}
