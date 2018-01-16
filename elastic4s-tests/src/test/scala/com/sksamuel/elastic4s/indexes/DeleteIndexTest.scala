package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.DockerTests
import com.sksamuel.elastic4s.http.ElasticDsl
import org.scalatest.{Matchers, WordSpec}

class DeleteIndexTest extends WordSpec with Matchers with DockerTests {

  "delete index request" should {
    "delete index" in {

      client.execute {
        createIndex("languages").mappings(
          mapping("dialects").fields(
            textField("type")
          )
        ).shards(1).waitForActiveShards(1)
      }.await

      client.execute {
        indexExists("languages")
      }.await.right.get.result.exists shouldBe true

      client.execute {
        ElasticDsl.deleteIndex("languages")
      }.await.right.get.result.acknowledged shouldBe true

      client.execute {
        indexExists("languages")
      }.await.right.get.result.exists shouldBe false
    }

    "support multiple indexes" in {
      client.execute {
        createIndex("languages1").mappings(
          mapping("dialects").fields(
            textField("type")
          )
        )
      }.await

      client.execute {
        createIndex("languages2").mappings(
          mapping("dialects").fields(
            textField("type")
          )
        )
      }.await

      client.execute {
        indexExists("languages1")
      }.await.right.get.result.exists shouldBe true

      client.execute {
        indexExists("languages2")
      }.await.right.get.result.exists shouldBe true

      client.execute {
        ElasticDsl.deleteIndex("languages1", "languages2")
      }.await.right.get.result.acknowledged shouldBe true

      client.execute {
        indexExists("languages1")
      }.await.right.get.result.exists shouldBe false

      client.execute {
        indexExists("languages2")
      }.await.right.get.result.exists shouldBe false
    }
  }
}
