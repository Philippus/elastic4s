package com.sksamuel.elastic4s.http.index.admin

import com.sksamuel.elastic4s.http.Shards

case class DeleteIndexResponse(acknowledged: Boolean)
case class RefreshIndexResponse()
case class OpenIndexResponse(acknowledged: Boolean)
case class CloseIndexResponse(acknowledged: Boolean)

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

case class IndicesAliasResponse(acknowledged: Boolean) {
  def success: Boolean = acknowledged
}

object IndexShardStoreResponse {
  case class StoreStatusResponse(indices: Map[String, IndexStoreStatus])
  case class IndexStoreStatus(shards: Map[String, ShardStoreStatus])
  type StoreStatus = Map[String, AnyRef]

  case class ShardStoreStatus(stores: Seq[StoreStatus])
}
