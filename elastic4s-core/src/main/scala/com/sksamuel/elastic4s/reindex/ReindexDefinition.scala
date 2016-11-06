package com.sksamuel.elastic4s.reindex

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.searches.QueryDefinition
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.action.support.ActiveShardCount
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.index.reindex.ReindexRequestBuilder

import scala.concurrent.duration.FiniteDuration

case class ReindexDefinition(sourceIndexes: Indexes,
                             targetIndex: String,
                             targetType: Option[String] = None,
                             filter: Option[QueryDefinition] = None,
                             requestsPerSecond: Option[Float] = None,
                             refresh: Option[Boolean] = None,
                             maxRetries: Option[Int] = None,
                             waitForActiveShards: Option[Int] = None,
                             timeout: Option[FiniteDuration] = None,
                             retryBackoffInitialTime: Option[FiniteDuration] = None,
                             shouldStoreResult: Option[Boolean] = None,
                             size: Option[Int] = None) {

  def populate(builder: ReindexRequestBuilder): Unit = {
    builder.source(sourceIndexes.values: _*)
    targetType.fold(builder.destination(targetIndex))(builder.destination(targetIndex, _))
    filter.map(_.builder).foreach(builder.filter)
    timeout.map(_.toNanos).map(TimeValue.timeValueNanos).foreach(builder.timeout)
    requestsPerSecond.foreach(builder.setRequestsPerSecond)
    maxRetries.foreach(builder.setMaxRetries)
    refresh.foreach(builder.refresh)
    waitForActiveShards.map(ActiveShardCount.from).foreach(builder.waitForActiveShards)
    retryBackoffInitialTime.map(_.toNanos).map(TimeValue.timeValueNanos).foreach(builder.setRetryBackoffInitialTime)
    shouldStoreResult.foreach(builder.setShouldStoreResult)
    size.foreach(builder.size)
  }

  def timeout(timeout: FiniteDuration): ReindexDefinition = copy(timeout = timeout.some)

  def refresh(refresh: Boolean): ReindexDefinition = copy(refresh = refresh.some)

  def filter(filter: QueryDefinition): ReindexDefinition = copy(filter = filter.some)

  def requestsPerSecond(requestsPerSecond: Float): ReindexDefinition =
    copy(requestsPerSecond = requestsPerSecond.some)

  def maxRetries(maxRetries: Int): ReindexDefinition = copy(maxRetries = maxRetries.some)

  def waitForActiveShards(waitForActiveShards: Int): ReindexDefinition =
    copy(waitForActiveShards = waitForActiveShards.some)

  def retryBackoffInitialTime(retryBackoffInitialTime: FiniteDuration): ReindexDefinition =
    copy(retryBackoffInitialTime = retryBackoffInitialTime.some)

  def size(size: Int): ReindexDefinition = copy(size = size.some)

  def shouldStoreResult(shouldStoreResult: Boolean): ReindexDefinition =
    copy(shouldStoreResult = shouldStoreResult.some)
}
