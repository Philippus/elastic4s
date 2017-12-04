package com.sksamuel.elastic4s.search.collapse

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{BeforeAndAfterAll, FreeSpec, Matchers}

import scala.util.Try

class CollapseHttpTest extends FreeSpec with Matchers with DiscoveryLocalNodeProvider with ElasticDsl with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = {

    Try {
      http.execute {
        deleteIndex("collapse")
      }.await
    }

    http.execute {
      createIndex("collapse") mappings {
        mapping("hotels") fields(
          keywordField("name"),
          keywordField("board")
        )
      }
    }.await

    http.execute {
      bulk(
        indexInto("collapse" / "hotels") id "1" fields("name" -> "Ibiza Playa", "board" -> "AI"),
        indexInto("collapse" / "hotels") id "2" fields("name" -> "Ibiza Playa", "board" -> "BB"),

        indexInto("collapse" / "hotels") id "3" fields("name" -> "Best Tenerife", "board" -> "AI")
      ).refresh(RefreshPolicy.Immediate)
    }.await
  }

  "collapse" - {
    "should be supported in http client" in {
      val resp = http.execute {
        search("collapse" / "hotels") collapse {
          collapseField("board")
        }
      }.await.get

      resp.totalHits shouldBe 3
      resp.hits.size shouldBe 2
    }
  }
}
