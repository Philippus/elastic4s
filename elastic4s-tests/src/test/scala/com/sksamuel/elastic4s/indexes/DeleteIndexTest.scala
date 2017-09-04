package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

class DeleteIndexTest extends WordSpec with Matchers with ElasticDsl with DiscoveryLocalNodeProvider {

  "delete index request" should {
    "delete index" in {

      http.execute {
        createIndex("languages").mappings(
          mapping("dialects").fields(
            textField("type")
          )
        ).shards(1).waitForActiveShards(1)
      }.await

      http.execute {
        indexExists("languages")
      }.await.exists shouldBe true

      http.execute {
        ElasticDsl.deleteIndex("languages")
      }.await.acknowledged shouldBe true

      http.execute {
        indexExists("languages")
      }.await.exists shouldBe false
    }

    "support multiple indexes" in {
      http.execute {
        createIndex("languages1").mappings(
          mapping("dialects").fields(
            textField("type")
          )
        )
      }.await

      http.execute {
        createIndex("languages2").mappings(
          mapping("dialects").fields(
            textField("type")
          )
        )
      }.await

      http.execute {
        indexExists("languages1")
      }.await.exists shouldBe true

      http.execute {
        indexExists("languages2")
      }.await.exists shouldBe true

      http.execute {
        ElasticDsl.deleteIndex("languages1", "languages2")
      }.await.acknowledged shouldBe true

      http.execute {
        indexExists("languages1")
      }.await.exists shouldBe false

      http.execute {
        indexExists("languages2")
      }.await.exists shouldBe false
    }
  }
}
