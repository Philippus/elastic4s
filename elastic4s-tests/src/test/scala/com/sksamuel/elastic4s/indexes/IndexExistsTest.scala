package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DualClientTests
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class IndexExistsTest extends WordSpec with Matchers with ElasticDsl with DualClientTests {

  override protected def beforeRunTests(): Unit = {

    Try {
      execute {
        deleteIndex("indexexists")
      }.await
    }

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
