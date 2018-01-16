package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._
import scala.util.Try

class RefreshIndexTest extends WordSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("beaches")
    }.await
  }

  client.execute {
    createIndex("beaches").mappings(
      mapping("dday").fields(
        textField("name")
      )
    ).shards(1).waitForActiveShards(1).refreshInterval(10.minutes)
  }.await

  "refresh index request" should {
    "refresh pending docs" in {

      client.execute {
        indexInto("beaches" / "dday").fields("name" -> "omaha")
      }.await

      // no data because the refresh is 10 minutes
      client.execute {
        search("beaches" / "dday").matchAllQuery()
      }.await.right.get.result.totalHits shouldBe 0

      client.execute {
        refreshIndex("beaches")
      }.await

      client.execute {
        search("beaches" / "dday").matchAllQuery()
      }.await.right.get.result.totalHits shouldBe 1
    }
  }
}
