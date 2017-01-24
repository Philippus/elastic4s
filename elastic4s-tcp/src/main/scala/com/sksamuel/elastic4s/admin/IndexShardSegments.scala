package com.sksamuel.elastic4s.admin

import org.elasticsearch.index.shard.ShardId

case class IndexShardSegments(original: org.elasticsearch.action.admin.indices.segments.IndexShardSegments) {
  def shards: Seq[ShardSegments] = Option(original.getShards).map(_.toSeq.map(ShardSegments.apply)).getOrElse(Nil)
  def shardId: ShardId = original.getShardId
}
