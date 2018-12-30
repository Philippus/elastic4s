package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class OpenCloseIndexRequestTest extends WordSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("pasta")
    }.await
  }

  client.execute {
    createIndex("pasta").mappings(
      mapping("types").fields(
        textField("name"),
        textField("region")
      )
    )
  }.await

  "close index" should {
    "acknowledge" in {
      client.execute {
        closeIndex("pasta")
      }.await.result.acknowledged shouldBe true
    }
  }

  "open index" should {
    "acknowledge" in {
      client.execute {
        openIndex("pasta")
      }.await.result.acknowledged shouldBe true
    }
  }
}
