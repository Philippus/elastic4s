package com.sksamuel.elastic4s.admin

import org.elasticsearch.action.ShardOperationFailedException
import org.elasticsearch.action.admin.indices.segments.IndicesSegmentResponse
import scala.collection.JavaConverters._

case class GetSegmentsResult(original: IndicesSegmentResponse) {

  def totalShards: Integer                              = original.getTotalShards
  def failedShards: Integer                             = original.getFailedShards
  def successfulShards: Integer                         = original.getSuccessfulShards
  def shardFailures: Seq[ShardOperationFailedException] = Option(original.getShardFailures).map(_.toSeq).getOrElse(Nil)

  def indices: Map[String, IndexSegments] =
    Option(original.getIndices).map(_.asScala.map { case (k, v) => k -> IndexSegments(v) }.toMap).getOrElse(Map.empty)
}
