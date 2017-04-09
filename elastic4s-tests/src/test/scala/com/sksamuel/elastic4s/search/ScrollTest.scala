package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import com.sksamuel.elastic4s.testkit.{DualClient, DualElasticSugar}
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._

class ScrollTest extends WordSpec with Matchers with ElasticDsl with DualElasticSugar with DualClient {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  override protected def beforeRunTests() = {
    execute {
      createIndex("katebush").mappings(
        mapping("songs").fields(
          intField("year"),
          textField("name").fielddata(true).stored(true)
        )
      )
    }.await

    execute {
      bulk(
        indexInto("katebush/songs").fields("name" -> "hounds of love", "year" -> "1985"),
        indexInto("katebush/songs").fields("name" -> "top of the city", "year" -> "1985"),
        indexInto("katebush/songs").fields("name" -> "wuthering heights", "year" -> "1979"),
        indexInto("katebush/songs").fields("name" -> "dream of sheep", "year" -> "1985"),
        indexInto("katebush/songs").fields("name" -> "waking the watch", "year" -> "1985"),
        indexInto("katebush/songs").fields("name" -> "watching you watching me", "year" -> "1985"),
        indexInto("katebush/songs").fields("name" -> "cloudbusting", "year" -> "1985"),
        indexInto("katebush/songs").fields("name" -> "under ice", "year" -> "1985"),
        indexInto("katebush/songs").fields("name" -> "jig of life", "year" -> "1985"),
        indexInto("katebush/songs").fields("name" -> "hello earth", "year" -> "1985")
      ).refresh(RefreshPolicy.IMMEDIATE)
    }.await
  }

  "a scroll" should {
    "return all results" in {

      val resp1 = execute {
        search("katebush" / "songs")
          .query("1985")
          .scroll("1m")
          .limit(2)
          .sortBy(fieldSort("name"))
          .storedFields("name")
      }.await
      resp1.hits.hits.map(_.storedField("name").value).toList shouldBe List("top of the city", "cloudbusting")

      val resp2 = execute {
        searchScroll(resp1.scrollId.get).keepAlive("1m")
      }.await
      resp2.hits.hits.map(_.storedField("name").value).toList shouldBe List("dream of sheep", "hello earth")

      val resp3 = execute {
        searchScroll(resp2.scrollId.get).keepAlive("1m")
      }.await
      resp3.hits.hits.map(_.storedField("name").value).toList shouldBe List("hounds of love", "under ice")

      val resp4 = execute {
        searchScroll(resp3.scrollId.get).keepAlive("1m")
      }.await
      resp4.hits.hits.map(_.storedField("name").value).toList shouldBe List("jig of life", "watching you watching me")

      val resp5 = execute {
        searchScroll(resp4.scrollId.get)
      }.await
      resp5.hits.hits.map(_.storedField("name").value).toList shouldBe List("waking the watch")
    }
  }

  "a 'searchScroll.keepAlive'" should {
    "not interpret FiniteDuration as 'id'" in {
      val resp1 = execute {
        search("katebush" / "songs")
          .query("1985")
          .scroll("1m")
          .limit(2)
          .sortBy(fieldSort("name"))
          .storedFields("name")
      }.await

      val resp2 = execute {
        searchScroll(resp1.scrollId.get).keepAlive(1.minute)
      }.await
      resp2.hits.hits.map(_.storedField("name").value).toList shouldBe List("dream of sheep", "hello earth")
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

      val resp1 = execute {
        searchDefinition
      }.await

      val resp2 = execute {
        searchDefinition
      }.await

      val resp = execute {
        clearScroll(resp1.scrollId.get, resp2.scrollId.get)
      }.await

      resp.succeeded should be(true)
      resp.num_freed should be > 0
    }
  }
}
