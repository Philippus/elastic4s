package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import org.scalatest.{WordSpec, Matchers}

class ScrollTest extends WordSpec with Matchers with ElasticSugar {

  client.execute {
    bulk(
      index into "katebush/songs" fields("name" -> "hounds of love", "year" -> "1985"),
      index into "katebush/songs" fields("name" -> "top of the city", "year" -> "1985"),
      index into "katebush/songs" fields("name" -> "wuthering heights", "year" -> "1979"),
      index into "katebush/songs" fields("name" -> "dream of sheep", "year" -> "1985"),
      index into "katebush/songs" fields("name" -> "hello earth", "year" -> "1985")
    )
  }.await

  refresh("katebush")
  blockUntilCount(5, "katebush")

  "a scroll" should {
    "return all results" in {

      val resp1 = client.execute {
        search in "katebush" / "songs" query "1985" scroll "1m" limit 2
      }.await
      resp1.getHits.getHits.size shouldBe 2

      val resp2 = client.execute {
        searchScroll(resp1.getScrollId).keepAlive("1m")
      }.await
      resp2.getHits.getHits.size shouldBe 2

      client.execute {
        searchScroll(resp2.getScrollId)
      }.await.getHits.getHits.size shouldBe 0
    }
  }
}
