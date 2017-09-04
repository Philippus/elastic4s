package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class FlushIndexTest extends WordSpec with Matchers with DiscoveryLocalNodeProvider with ElasticDsl {

  private val indexname = "flushindextest"

  Try {
    http.execute {
      deleteIndex(indexname)
    }
  }

  http.execute {
    createIndex(indexname).mappings(
      mapping("pasta").fields(
        textField("name")
      )
    )
  }.await

  "flush index" should {
    "acknowledge" in {
      http.execute {
        flushIndex(indexname)
      }.await.shards.successful > 0 shouldBe true
    }
  }
}
