package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._
import scala.util.Try

class RefreshIndexRequestTest extends WordSpec with Matchers with DockerTests {

  Try {
    http.execute {
      deleteIndex("refreshtest")
    }.await
  }

  http.execute {
    createIndex("refreshtest").mappings(
      mapping("dday").fields(
        textField("name")
      )
    ).shards(1).waitForActiveShards(1).refreshInterval(10.minutes)
  }.await

  "refresh index request" should {
    "refresh pending docs" in {

      http.execute {
        indexInto("refreshtest/dday").fields("name" -> "omaha")
      }.await

      // no data because the refresh is 10 minutes
      http.execute {
        search("refreshtest").matchAllQuery()
      }.await.right.get.result.totalHits shouldBe 0

      http.execute {
        refreshIndex("refreshtest")
      }.await

      http.execute {
        search("refreshtest").matchAllQuery()
      }.await.right.get.result.totalHits shouldBe 1
    }
  }
}
