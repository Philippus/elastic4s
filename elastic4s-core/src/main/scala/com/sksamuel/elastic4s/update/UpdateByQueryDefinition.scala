package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.elastic4s.{Indexes, IndexesAndTypes, RefreshPolicy}
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration.FiniteDuration

case class UpdateByQueryDefinition(indexesAndTypes: IndexesAndTypes,
                                   query: QueryDefinition,
                                   requestsPerSecond: Option[Float] = None,
                                   maxRetries: Option[Int] = None,
                                   proceedOnConflicts: Option[Boolean] = None,
                                   pipeline: Option[String] = None,
                                   refresh: Option[RefreshPolicy] = None,
                                   script: Option[ScriptDefinition] = None,
                                   waitForActiveShards: Option[Int] = None,
                                   retryBackoffInitialTime: Option[FiniteDuration] = None,
                                   scrollSize: Option[Int] = None,
                                   timeout: Option[FiniteDuration] = None,
                                   shouldStoreResult: Option[Boolean] = None,
                                   size: Option[Int] = None) {

  def proceedOnConflicts(proceedOnConflicts: Boolean): UpdateByQueryDefinition =
    copy(proceedOnConflicts = proceedOnConflicts.some)

  @deprecated("use proceedOnConflicts")
  def abortOnVersionConflict(abortOnVersionConflict: Boolean): UpdateByQueryDefinition =
    proceedOnConflicts(abortOnVersionConflict)

  def refresh(refresh: RefreshPolicy): UpdateByQueryDefinition = copy(refresh = refresh.some)
  def refreshImmediately                                       = refresh(RefreshPolicy.IMMEDIATE)

  def scrollSize(scrollSize: Int): UpdateByQueryDefinition = copy(scrollSize = scrollSize.some)

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

object UpdateByQueryDefinition {
  def apply(indexes: Indexes, query: QueryDefinition) = new UpdateByQueryDefinition(indexes.toIndexesAndTypes, query)
}
