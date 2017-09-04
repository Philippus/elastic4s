package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class IndexExistsTest extends WordSpec with Matchers with ElasticDsl with DiscoveryLocalNodeProvider {

  Try {
    http.execute {
      deleteIndex("indexexists")
    }.await
  }

  http.execute {
    createIndex("indexexists").mappings {
      mapping("flowers") fields textField("name")
    }
  }.await

  "an index exists request" should {
    "return true for an existing index" in {
      http.execute {
        indexExists("indexexists")
      }.await.isExists shouldBe true
    }
    "return false for non existing index" in {
      http.execute {
        indexExists("qweqwewqe")
      }.await.isExists shouldBe false
    }
  }
}
