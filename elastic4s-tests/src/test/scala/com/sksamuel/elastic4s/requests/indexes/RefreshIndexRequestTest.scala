package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._
import scala.util.Try

class RefreshIndexRequestTest extends WordSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("refreshtest")
    }.await
  }

  client.execute {
    createIndex("refreshtest").mappings(
      mapping("dday").fields(
        textField("name")
      )
    ).shards(1).waitForActiveShards(1).refreshInterval(10.minutes)
  }.await

  "refresh index request" should {
    "refresh pending docs" in {

      client.execute {
        indexInto("refreshtest/dday").fields("name" -> "omaha")
      }.await

      // no data because the refresh is 10 minutes
      client.execute {
        search("refreshtest").matchAllQuery()
      }.await.result.totalHits shouldBe 0

      client.execute {
        refreshIndex("refreshtest")
      }.await

      client.execute {
        search("refreshtest").matchAllQuery()
      }.await.result.totalHits shouldBe 1
    }
  }
}
