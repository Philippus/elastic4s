package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import com.sksamuel.elastic4s.testkit.{DualClient, DualElasticSugar}
import org.scalatest.{Matchers, WordSpec}

class IndexExistsTest extends WordSpec with Matchers with ElasticDsl with DualElasticSugar with DualClient {

  override protected def beforeRunTests() = {
    execute {
      createIndex("indexexists").mappings {
        mapping("flowers") fields textField("name")
      }
    }.await
  }

  "an index exists request" should {
    "return true for an existing index" in {
      execute {
        indexExists("indexexists")
      }.await.isExists shouldBe true
    }
    "return false for non existing index" in {
      execute {
        indexExists("qweqwewqe")
      }.await.isExists shouldBe false
    }
  }
}
