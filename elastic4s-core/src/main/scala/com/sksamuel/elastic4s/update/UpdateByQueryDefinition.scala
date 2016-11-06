package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.QueryDefinition
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.action.support.ActiveShardCount
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder

import scala.concurrent.duration.FiniteDuration

case class UpdateByQueryDefinition(sourceIndexes: Indexes,
                                   query: QueryDefinition,
                                   requestsPerSecond: Option[Float] = None,
                                   maxRetries: Option[Int] = None,
                                   abortOnVersionConflict: Option[Boolean] = None,
                                   pipeline: Option[String] = None,
                                   refresh: Option[Boolean] = None,
                                   script: Option[ScriptDefinition] = None,
                                   waitForActiveShards: Option[Int] = None,
                                   retryBackoffInitialTime: Option[FiniteDuration] = None,
                                   timeout: Option[FiniteDuration] = None,
                                   shouldStoreResult: Option[Boolean] = None,
                                   size: Option[Int] = None) {

  def populate(builder: UpdateByQueryRequestBuilder): Unit = {
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
    pipeline.foreach(builder.setPipeline)
    script.map(_.build).foreach(builder.script)
  }

  def abortOnVersionConflict(abortOnVersionConflict: Boolean): UpdateByQueryDefinition =
    copy(abortOnVersionConflict = abortOnVersionConflict.some)

  def refresh(refresh: Boolean): UpdateByQueryDefinition = copy(refresh = refresh.some)

  def requestsPerSecond(requestsPerSecond: Float): UpdateByQueryDefinition =
    copy(requestsPerSecond = requestsPerSecond.some)

  def maxRetries(maxRetries: Int): UpdateByQueryDefinition = copy(maxRetries = maxRetries.some)

  def waitForActiveShards(waitForActiveShards: Int): UpdateByQueryDefinition =
    copy(waitForActiveShards = waitForActiveShards.some)

  def retryBackoffInitialTime(retryBackoffInitialTime: FiniteDuration): UpdateByQueryDefinition =
    copy(retryBackoffInitialTime = retryBackoffInitialTime.some)

  def timeout(timeout: FiniteDuration): UpdateByQueryDefinition = copy(timeout = timeout.some)

  def size(size: Int): UpdateByQueryDefinition = copy(size = size.some)

  def script(script: ScriptDefinition): UpdateByQueryDefinition = copy(script = script.some)

  def shouldStoreResult(shouldStoreResult: Boolean): UpdateByQueryDefinition =
    copy(shouldStoreResult = shouldStoreResult.some)

}

