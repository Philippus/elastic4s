package com.sksamuel.elastic4s.admin

import org.elasticsearch.action.ShardOperationFailedException
import org.elasticsearch.action.admin.indices.segments.IndicesSegmentResponse
import scala.collection.JavaConverters._

case class GetSegmentsResult(original: IndicesSegmentResponse) {

  @deprecated("Use the scala idiomatic methods", "2.0")
  def getTotalShards() = original.getTotalShards

  @deprecated("Use the scala idiomatic methods", "2.0")
  def getFailedShards() = original.getFailedShards

  @deprecated("Use the scala idiomatic methods", "2.0")
  def getSuccessfulShards() = original.getSuccessfulShards

  @deprecated("Use the scala idiomatic methods", "2.0")
  def getShardFailures() = original.getShardFailures

  @deprecated("Use the scala idiomatic methods", "2.0")
  def getIndices() = original.getIndices

  def totalShards: Integer = original.getTotalShards
  def failedShards: Integer = original.getFailedShards
  def successfulShards: Integer = original.getSuccessfulShards
  def shardFailures: Seq[ShardOperationFailedException] = Option(original.getShardFailures).map(_.toSeq).getOrElse(Nil)

  def indices: Map[String, IndexSegments] = {
    Option(original.getIndices).map(_.asScala.map { case (k, v) => k -> IndexSegments(v) }.toMap).getOrElse(Map.empty)
  }
}
