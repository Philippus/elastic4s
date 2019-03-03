package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.requests.admin.{IndicesExistsRequest, IndicesOptionsRequest}
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

  "an index exist request with wildcard" should {
    "return true when no indices were found and allowNoIndices=true" in {
      client.execute {
        IndicesExistsRequest(indexes = "qweqwe*", indicesOptions = Some(IndicesOptionsRequest(allowNoIndices = true)))
      }.await.result.isExists shouldBe true
    }
    "return false when no indices were found and allowNoIndices=false" in {
      client.execute {
        IndicesExistsRequest(indexes = "qweqwe*", indicesOptions = Some(IndicesOptionsRequest(allowNoIndices = false)))
      }.await.result.isExists shouldBe false
    }
  }
}
