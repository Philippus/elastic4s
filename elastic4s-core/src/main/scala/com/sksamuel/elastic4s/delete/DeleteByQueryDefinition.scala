package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.searches.QueryDefinition
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.action.support.ActiveShardCount
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder

import scala.concurrent.duration.FiniteDuration

case class DeleteByQueryDefinition(sourceIndexes: Indexes,
                                   query: QueryDefinition,
                                   requestsPerSecond: Option[Float] = None,
                                   maxRetries: Option[Int] = None,
                                   abortOnVersionConflict: Option[Boolean] = None,
                                   refresh: Option[Boolean] = None,
                                   waitForActiveShards: Option[Int] = None,
                                   retryBackoffInitialTime: Option[FiniteDuration] = None,
                                   timeout: Option[FiniteDuration] = None,
                                   shouldStoreResult: Option[Boolean] = None,
                                   size: Option[Int] = None) {

  def populate(builder: DeleteByQueryRequestBuilder) = {
    builder.source(sourceIndexes.values: _*)
    builder.filter(query.builder)
    requestsPerSecond.foreach(builder.setRequestsPerSecond)
    maxRetries.foreach(builder.setMaxRetries)
    refresh.foreach(builder.refresh)
    waitForActiveShards.map(ActiveShardCount.from).foreach(builder.waitForActiveShards)
    timeout.map(_.toNanos).map(TimeValue.timeValueNanos).foreach(builder.timeout)
    retryBackoffInitialTime.map(_.toNanos).map(TimeValue.timeValueNanos).foreach(builder.setRetryBackoffInitialTime)
    shouldStoreResult.foreach(builder.setShouldStoreResult)
    abortOnVersionConflict.foreach(builder.abortOnVersionConflict)
  }

  def abortOnVersionConflict(abortOnVersionConflict: Boolean): DeleteByQueryDefinition =
    copy(abortOnVersionConflict = abortOnVersionConflict.some)

  def refresh(refresh: Boolean): DeleteByQueryDefinition = copy(refresh = refresh.some)

  def requestsPerSecond(requestsPerSecond: Float): DeleteByQueryDefinition =
    copy(requestsPerSecond = requestsPerSecond.some)

  def maxRetries(maxRetries: Int): DeleteByQueryDefinition = copy(maxRetries = maxRetries.some)

  def waitForActiveShards(waitForActiveShards: Int): DeleteByQueryDefinition =
    copy(waitForActiveShards = waitForActiveShards.some)

  def retryBackoffInitialTime(retryBackoffInitialTime: FiniteDuration): DeleteByQueryDefinition =
    copy(retryBackoffInitialTime = retryBackoffInitialTime.some)

  def timeout(timeout: FiniteDuration): DeleteByQueryDefinition = copy(timeout = timeout.some)

  def size(size: Int): DeleteByQueryDefinition = copy(size = size.some)

  def shouldStoreResult(shouldStoreResult: Boolean): DeleteByQueryDefinition =
    copy(shouldStoreResult = shouldStoreResult.some)

}

