package com.sksamuel.elastic4s.search.collapse

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{BeforeAndAfterAll, FreeSpec, Matchers}

import scala.util.Try

class CollapseHttpTest extends FreeSpec with Matchers with DockerTests with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = {

    Try {
      client.execute {
        deleteIndex("collapse")
      }.await
    }

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
        indexInto("collapse" / "hotels") id "1" fields("name" -> "Ibiza Playa", "board" -> "AI"),
        indexInto("collapse" / "hotels") id "2" fields("name" -> "Ibiza Playa", "board" -> "BB"),

        indexInto("collapse" / "hotels") id "3" fields("name" -> "Best Tenerife", "board" -> "AI")
      ).refresh(RefreshPolicy.Immediate)
    }.await
  }

  "collapse" - {
    "should be supported in http client" in {
      val resp = client.execute {
        search("collapse" / "hotels") collapse {
          collapseField("board")
        }
      }.await.result

      resp.totalHits shouldBe 3
      resp.hits.size shouldBe 2
    }
  }
}
