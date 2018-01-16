package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class IndexExistsTest extends WordSpec with Matchers with DockerTests {

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
      }.await.right.get.result.isExists shouldBe true
    }
    "return false for non existing index" in {
      http.execute {
        indexExists("qweqwewqe")
      }.await.right.get.result.isExists shouldBe false
    }
  }
}
