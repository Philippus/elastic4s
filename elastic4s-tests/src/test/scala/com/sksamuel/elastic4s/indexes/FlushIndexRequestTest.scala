package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class FlushIndexRequestTest extends WordSpec with Matchers with DockerTests {

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
      }.await.result.shards.successful > 0 shouldBe true
    }
  }
}
