package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._
import scala.util.Try

class RefreshIndexTest extends WordSpec with Matchers with ElasticDsl with DiscoveryLocalNodeProvider {

  Try {
    http.execute {
      deleteIndex("beaches")
    }.await
  }

  http.execute {
    createIndex("beaches").mappings(
      mapping("dday").fields(
        textField("name")
      )
    ).shards(1).waitForActiveShards(1).refreshInterval(10.minutes)
  }.await

  "refresh index request" should {
    "refresh pending docs" in {

      http.execute {
        indexInto("beaches" / "dday").fields("name" -> "omaha")
      }.await

      // no data because the refresh is 10 minutes
      http.execute {
        search("beaches" / "dday").matchAllQuery()
      }.await.right.get.totalHits shouldBe 0

      http.execute {
        refreshIndex("beaches")
      }.await

      http.execute {
        search("beaches" / "dday").matchAllQuery()
      }.await.right.get.totalHits shouldBe 1
    }
  }
}
