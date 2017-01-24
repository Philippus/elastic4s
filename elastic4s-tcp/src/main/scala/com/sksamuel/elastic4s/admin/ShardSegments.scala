package com.sksamuel.elastic4s.admin

import org.elasticsearch.index.engine.Segment

case class ShardSegments(original: org.elasticsearch.action.admin.indices.segments.ShardSegments) {

  import scala.collection.JavaConverters._

  def numberOfCommitted: Integer = original.getNumberOfCommitted
  def numberOfSearch: Integer = original.getNumberOfSearch
  def segments: Seq[Segment] = Option(original.getSegments).map(_.asScala).getOrElse(Nil)
  def shardRouting = original.getShardRouting
}
