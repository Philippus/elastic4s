package com.sksamuel.elastic4s.search.collapse

import com.sksamuel.elastic4s.testkit.{ElasticSugar, SharedElasticSugar}
import org.scalatest.{BeforeAndAfterAll, FreeSpec, Matchers}

class CollapseTest extends FreeSpec with Matchers with SharedElasticSugar with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = {
    client.execute {
      createIndex("collapse") mappings {
        mapping("hotels") fields(
          keywordField("name"),
          keywordField("board")
        )
      }
    }.await

    client.execute {
      bulk(
        indexInto("collapse" / "hotels") id "1" fields("name" -> "Ibiza Playa", "board" -> "AI", "price" -> 150, "rating" -> 7),
        indexInto("collapse" / "hotels") id "2" fields("name" -> "Ibiza Playa", "board" -> "BB", "price" -> 120, "rating" -> 7),

        indexInto("collapse" / "hotels") id "3" fields("name" -> "Best Tenerife", "board" -> "AI", "price" -> 220, "rating" -> 9),
        indexInto("collapse" / "hotels") id "4" fields("name" -> "Best Tenerife", "board" -> "HP", "price" -> 210, "rating" -> 9),
        indexInto("collapse" / "hotels") id "5" fields("name" -> "Best Tenerife", "board" -> "BB", "price" -> 180, "rating" -> 9),

        indexInto("collapse" / "hotels") id "6" fields("name" -> "Parque Santiago", "board" -> "AI", "price" -> 170, "rating" -> 8),

        indexInto("collapse" / "hotels") id "7" fields("name" -> "Palma Bay", "board" -> "BB", "price" -> 100, "rating" -> 4)
      )
    }.await

    refresh("collapse")
    blockUntilCount(7, "collapse")
  }

  "collapse" - {

    /**
      * The total number of hits in the response indicates the number of matching documents without collapsing.
      * The total number of distinct group is unknown.
      */
    "should preform collapsing on field" in {
      val resp = client.execute {
        search("collapse" / "hotels") collapse {
          collapseField("name")
        } sortByFieldAsc "price"
      }.await

      resp.hits.length shouldBe 4
      resp.totalHits shouldBe 7L

      resp.hits.map(_.java.id) shouldBe Array("7", "2", "6", "5")
    }

    "should support inner hits" in {
      val resp = client.execute {
        search("collapse" / "hotels") collapse {
          collapseField("name") inner {
            innerHits("by-price") size 1 sortBy fieldSort("price")
          }
        } sortByFieldDesc "rating"
      }.await

      resp.hits.map(_.java.id) shouldBe Array("5", "6", "2", "7")

      val inner = resp.hits(0).java.getInnerHits.get("by-price")
      inner.hits.length shouldBe 1
      inner.totalHits shouldBe 3L
      inner.getHits.head.id shouldBe "5"
    }
  }
}
