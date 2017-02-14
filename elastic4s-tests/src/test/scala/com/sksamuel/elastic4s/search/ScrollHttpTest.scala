package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._

class ScrollHttpTest extends WordSpec with Matchers with SharedElasticSugar with ElasticDsl {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    createIndex("scrolltest").mappings(
      mapping("songs").fields(
        intField("year"),
        textField("name").fielddata(true).stored(true)
      )
    )
  }.await

  http.execute {
    bulk(
      indexInto("scrolltest/songs").fields("name" -> "hounds of love", "year" -> "1985"),
      indexInto("scrolltest/songs").fields("name" -> "top of the city", "year" -> "1985"),
      indexInto("scrolltest/songs").fields("name" -> "wuthering heights", "year" -> "1979"),
      indexInto("scrolltest/songs").fields("name" -> "dream of sheep", "year" -> "1985"),
      indexInto("scrolltest/songs").fields("name" -> "waking the watch", "year" -> "1985"),
      indexInto("scrolltest/songs").fields("name" -> "watching you watching me", "year" -> "1985"),
      indexInto("scrolltest/songs").fields("name" -> "cloudbusting", "year" -> "1985"),
      indexInto("scrolltest/songs").fields("name" -> "under ice", "year" -> "1985"),
      indexInto("scrolltest/songs").fields("name" -> "jig of life", "year" -> "1985"),
      indexInto("scrolltest/songs").fields("name" -> "hello earth", "year" -> "1985")
    ).refresh(RefreshPolicy.IMMEDIATE)
  }.await

  "a scroll" should {
    "return all results" in {

      val resp1 = http.execute {
        search("scrolltest" / "songs")
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
        search("scrolltest" / "songs")
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
}
