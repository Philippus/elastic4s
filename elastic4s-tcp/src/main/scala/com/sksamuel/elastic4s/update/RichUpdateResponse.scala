package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.DocumentRef
import org.elasticsearch.action.DocWriteResponse.Result
import org.elasticsearch.action.support.replication.ReplicationResponse.ShardInfo
import org.elasticsearch.action.update.UpdateResponse
import org.elasticsearch.index.get.GetResult
import org.elasticsearch.index.shard.ShardId
import org.elasticsearch.rest.RestStatus

case class RichUpdateResponse(original: UpdateResponse) {
  def result: Result       = original.getResult
  def get: GetResult       = original.getGetResult
  def status: RestStatus   = original.status()
  def index: String        = original.getIndex
  def `type`: String       = original.getType
  def id: String           = original.getId
  def ref: DocumentRef     = DocumentRef(index, `type`, id)
  def shardId: ShardId     = original.getShardId
  def shardInfo: ShardInfo = original.getShardInfo
  def version: Long        = original.getVersion
  def created: Boolean     = original.getResult == Result.CREATED
}
