package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class IndexExistsTest extends WordSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("indexexists")
    }.await
  }

  client.execute {
    createIndex("indexexists").mappings {
      mapping("flowers") fields textField("name")
    }
  }.await

  "an index exists request" should {
    "return true for an existing index" in {
      client.execute {
        indexExists("indexexists")
      }.await.result.isExists shouldBe true
    }
    "return false for non existing index" in {
      client.execute {
        indexExists("qweqwewqe")
      }.await.result.isExists shouldBe false
    }
  }
}
