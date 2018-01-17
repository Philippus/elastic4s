package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.http.index.admin.IndexShardStoreResponse.{IndexStoreStatus, ShardStoreStatus}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class ShardStoreHttpTest extends WordSpec with Matchers with DockerTests {

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
    ).shards(1).replicas(0).waitForActiveShards(1)
  }.await

  "shard store request" should {
    "get green shards" in {

      val indexInfo = http.execute {
        indexShardStores("beaches") status "green"
      }.await.right.get.result.indices.getOrElse("beaches", IndexStoreStatus(Map.empty))

      val shardInfo: ShardStoreStatus = indexInfo.shards.getOrElse("0", ShardStoreStatus(Seq.empty))
      shardInfo.stores.size should be(1)
    }
  }
}
