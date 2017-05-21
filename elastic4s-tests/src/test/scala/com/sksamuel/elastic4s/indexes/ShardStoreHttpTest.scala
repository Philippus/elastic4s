package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.http.index.admin.IndexShardStoreResponse.{IndexStoreStatus, ShardStoreStatus}
import com.sksamuel.elastic4s.testkit.ClassloaderLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

class ShardStoreHttpTest extends WordSpec with Matchers with ClassloaderLocalNodeProvider with ElasticDsl {

  "shard store request" should {
    "get green shards" in {
      http.execute {
        createIndex("beaches").mappings(
          mapping("dday").fields(
            textField("name")
          )
        ).shards(1).replicas(0).waitForActiveShards(1)
      }.await

      val indexInfo = http.execute {
        indexShardStores("beaches") status "green"
      }.await.indices.getOrElse("beaches", IndexStoreStatus(Map.empty))

      val shardInfo: ShardStoreStatus = indexInfo.shards.getOrElse("0", ShardStoreStatus(Seq.empty))
      shardInfo.stores.size should be(1)
    }
  }
}
