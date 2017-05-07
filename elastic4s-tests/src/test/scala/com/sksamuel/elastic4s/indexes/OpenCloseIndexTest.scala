package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import com.sksamuel.elastic4s.testkit.{DualClient, DualElasticSugar}
import org.scalatest.{Matchers, WordSpec}

class OpenCloseIndexTest extends WordSpec with Matchers with ElasticDsl with DualElasticSugar with DualClient {

  override protected def beforeRunTests() = {
    execute {
      createIndex("pasta").mappings(
        mapping("types").fields(
          textField("name"),
          textField("region")
        )
      )
    }.await
  }

  "close index" should {
    "acknowledge" in {
      execute {
        closeIndex("pasta")
      }.await.acknowledged shouldBe true
    }
  }

  "open index" should {
    "acknowledge" in {
      execute {
        openIndex("pasta")
      }.await.acknowledged shouldBe true
    }
  }
}
