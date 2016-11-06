package com.sksamuel.elastic4s.reindex

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.searches.QueryDefinition
import org.elasticsearch.action.support.ActiveShardCount
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.index.reindex.ReindexRequestBuilder

import scala.concurrent.duration.FiniteDuration

case class ReindexDefinition(sourceIndexes: Indexes,
                             targetIndex: String,
                             targetType: Option[String] = None,
                             filter: Option[QueryDefinition] = None,
                             requestsPerSecond: Option[Float] = None,
                             maxRetries: Option[Int] = None,
                             waitForActiveShards: Option[Int] = None,
                             retryBackoffInitialTime: Option[FiniteDuration] = None,
                             shouldStoreResult: Option[Boolean] = None,
                             size: Option[Int] = None) {

  def populate(builder: ReindexRequestBuilder): Unit = {
    builder.source(sourceIndexes.values: _*)
    targetType.fold(builder.destination(targetIndex))(builder.destination(targetIndex, _))
    filter.map(_.builder).foreach(builder.filter)
    requestsPerSecond.foreach(builder.setRequestsPerSecond)
    maxRetries.foreach(builder.setMaxRetries)
    waitForActiveShards.map(ActiveShardCount.from).foreach(builder.waitForActiveShards)
    retryBackoffInitialTime.map(_.toNanos).map(TimeValue.timeValueNanos).foreach(builder.setRetryBackoffInitialTime)
    shouldStoreResult.foreach(builder.setShouldStoreResult)
    size.foreach(builder.size)
  }
}


