package com.sksamuel.elastic4s.requests.cluster

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.cluster.ClusterStatsResponse.Indices

case class ClusterStatsRequest()

object ClusterStatsResponse {

  case class ShardStats(min: Int, max: Int, avg: Int)

  case class Index(shards: ShardStats, primaries: ShardStats, replication: ShardStats)

  case class Shards(total: Int, primaries: Int, replication: Int, index: Index)

  case class Docs(count: Long, deleted: Long)

  case class Store(@JsonProperty("size_in_bytes") sizeInBytes: Long, size: String)

  case class FieldData(
                        @JsonProperty("memory_size") memorySize: String,
                        @JsonProperty("memory_size_in_bytes") memorySizeInBytes: Long,
                        @JsonProperty("evictions") evictions: Long
                      )

  case class QueryCache(
                         @JsonProperty("memory_size") memory_size: String,
                         @JsonProperty("memory_size_in_bytes") memorySizeInBytes: Long,
                         @JsonProperty("total_count") totalTount: Long,
                         @JsonProperty("hit_count") hitCount: Long,
                         @JsonProperty("miss_count") missCount: Long,
                         @JsonProperty("cache_size") cacheSize: Long,
                         @JsonProperty("cache_count") cacheCount: Long,
                         @JsonProperty("evictions") evictions: Long,
                       )

  case class Indices(count: Int,
                     shards: Shards,
                     docs: Docs,
                     store: Store,
                     @JsonProperty("fielddata") fieldData: FieldData,
                     @JsonProperty("query_cache") queryCache: QueryCache
                    )
}

case class ClusterStatsResponse(@JsonProperty("cluster_name") clusterName: String,
                                @JsonProperty("cluster_uuid") clusterUUID: String,
                                @JsonProperty("timestamp") timestamp: Long,
                                @JsonProperty("status") status: String,
                                @JsonProperty("indices") indices: Indices)


