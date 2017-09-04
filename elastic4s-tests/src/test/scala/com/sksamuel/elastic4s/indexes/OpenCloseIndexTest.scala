package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class OpenCloseIndexTest extends WordSpec with Matchers with ElasticDsl with DiscoveryLocalNodeProvider {

  Try {
    http.execute {
      deleteIndex("pasta")
    }.await
  }

  http.execute {
    createIndex("pasta").mappings(
      mapping("types").fields(
        textField("name"),
        textField("region")
      )
    )
  }.await

  "close index" should {
    "acknowledge" in {
      http.execute {
        closeIndex("pasta")
      }.await.acknowledged shouldBe true
    }
  }

  "open index" should {
    "acknowledge" in {
      http.execute {
        openIndex("pasta")
      }.await.acknowledged shouldBe true
    }
  }
}
