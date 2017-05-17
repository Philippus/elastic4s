package com.sksamuel.elastic4s.admin

import org.elasticsearch.action.admin.indices.stats.{CommonStats, IndexStats, IndicesStatsResponse, ShardStats}
import org.elasticsearch.cluster.routing.ShardRouting

case class IndicesStatsResult(original: IndicesStatsResponse) {

  import scala.collection.JavaConverters._

  def primaries: CommonStats = original.getPrimaries
  def routing: Map[ShardRouting, ShardStats] = original.asMap.asScala.toMap
  def indexStats: Map[String, IndexStats] = original.getIndices.asScala.toMap
  def totalStats: CommonStats = original.getTotal
  def shardStats: Seq[org.elasticsearch.action.admin.indices.stats.ShardStats] = original.getShards.toSeq
  def indexNames: Set[String] = indexStats.keySet
}
