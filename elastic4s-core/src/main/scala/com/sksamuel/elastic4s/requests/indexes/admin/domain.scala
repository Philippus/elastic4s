package com.sksamuel.elastic4s.requests.indexes.admin

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.common.Shards

case class DeleteIndexResponse(acknowledged: Boolean)
case class RefreshIndexResponse()
case class OpenIndexResponse(acknowledged: Boolean)
case class CloseIndexResponse(acknowledged: Boolean)

case class GetSegmentsResponse(shards: Shards, indices: Map[String, IndexShards])

case class IndexShards(shards: Map[String, Seq[Shard]])

case class Shard(@JsonProperty("num_committed_segments") numCommittedSegments: Long,
                 @JsonProperty("num_search_segments") numSearchSegments: Long,
                 routing: Routing,
                 segments: Map[String, Seq[Segment]])

case class Segment(generation: Long,
                   @JsonProperty("num_docs") numDocs: Long,
                   @JsonProperty("deleted_docs") deletedDocs: Long,
                   @JsonProperty("size_in_bytes") sizeInBytes: Long,
                   @JsonProperty("memory_in_bytes") memoryInBytes: Long,
                   committed: Boolean,
                   search: Boolean,
                   version: String,
                   compound: Boolean)

case class Routing(state: String, primary: Boolean, node: String)

case class ForceMergeResponse()
case class IndexRecoveryResponse()

case class FlushIndexResponse(_shards: Shards) {
  def shards: Shards = _shards
}

case class TypeExistsResponse(exists: Boolean) {
  def isExists: Boolean = exists
}

case class IndexExistsResponse(exists: Boolean) {
  def isExists: Boolean = exists
}

case class AliasExistsResponse(exists: Boolean) {
  def isExists: Boolean = exists
}

case class ClearCacheResponse(_shards: Shards) {
  def shards: Shards = _shards
}

case class UpdateIndexLevelSettingsResponse(acknowledged: Boolean) {
  def success: Boolean = acknowledged
}

case class AliasActionResponse(acknowledged: Boolean) {
  def success: Boolean = acknowledged
}

object IndexShardStoreResponse {
  case class StoreStatusResponse(indices: Map[String, IndexStoreStatus])
  case class IndexStoreStatus(shards: Map[String, ShardStoreStatus])
  type StoreStatus = Map[String, AnyRef]

  case class ShardStoreStatus(stores: Seq[StoreStatus])
}
