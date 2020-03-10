package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.testkit.DockerTests

import scala.util.Try
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class FlushIndexRequestTest extends AnyWordSpec with Matchers with DockerTests {

  private val indexname = "flushindextest"

  Try {
    client.execute {
      deleteIndex(indexname)
    }
  }

  client.execute {
    createIndex(indexname).mapping(
      properties(
        textField("name")
      )
    )
  }.await

  "flush index" should {
    "acknowledge" in {
      client.execute {
        flushIndex(indexname)
      }.await.result.shards.successful > 0 shouldBe true
    }
  }
}
