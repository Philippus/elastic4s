package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import com.sksamuel.elastic4s.testkit.{DualClient, DualElasticSugar}
import org.scalatest.{Matchers, WordSpec}

class DeleteIndexTest extends WordSpec with Matchers with ElasticDsl with DualElasticSugar with DualClient {

  "delete index request" should {
    "delete index" in {

      execute {
        createIndex("languages").mappings(
          mapping("dialects").fields(
            textField("type")
          )
        ).shards(1).waitForActiveShards(1)
      }.await

      execute {
        indexExists("languages")
      }.await.exists shouldBe true

      execute {
        ElasticDsl.deleteIndex("languages")
      }.await.acknowledged shouldBe true

      execute {
        indexExists("languages")
      }.await.exists shouldBe false
    }

    "support multiple indexes" in {
      execute {
        createIndex("languages1").mappings(
          mapping("dialects").fields(
            textField("type")
          )
        )
      }.await

      execute {
        createIndex("languages2").mappings(
          mapping("dialects").fields(
            textField("type")
          )
        )
      }.await

      execute {
        indexExists("languages1")
      }.await.exists shouldBe true

      execute {
        indexExists("languages2")
      }.await.exists shouldBe true

      execute {
        ElasticDsl.deleteIndex("languages1", "languages2")
      }.await.acknowledged shouldBe true

      execute {
        indexExists("languages1")
      }.await.exists shouldBe false

      execute {
        indexExists("languages2")
      }.await.exists shouldBe false
    }
  }
}
