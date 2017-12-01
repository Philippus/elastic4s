package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.{IndexesAndTypes, RefreshPolicy}
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration.FiniteDuration

case class DeleteByQueryDefinition(indexesAndTypes: IndexesAndTypes,
                                   query: QueryDefinition,
                                   requestsPerSecond: Option[Float] = None,
                                   maxRetries: Option[Int] = None,
                                   proceedOnConflicts: Option[Boolean] = None,
                                   refresh: Option[RefreshPolicy] = None,
                                   waitForActiveShards: Option[Int] = None,
                                   retryBackoffInitialTime: Option[FiniteDuration] = None,
                                   timeout: Option[FiniteDuration] = None,
                                   scrollSize: Option[Int] = None,
                                   shouldStoreResult: Option[Boolean] = None,
                                   size: Option[Int] = None) {

  def proceedOnConflicts(proceedOnConflicts: Boolean): DeleteByQueryDefinition =
    copy(proceedOnConflicts = proceedOnConflicts.some)

  @deprecated("use proceedOnConflicts")
  def abortOnVersionConflict(abortOnVersionConflict: Boolean): DeleteByQueryDefinition =
    proceedOnConflicts(abortOnVersionConflict)

  def refresh(refresh: RefreshPolicy): DeleteByQueryDefinition = copy(refresh = refresh.some)
  def refreshImmediately = refresh(RefreshPolicy.IMMEDIATE)

  def scrollSize(scrollSize: Int): DeleteByQueryDefinition = copy(scrollSize = scrollSize.some)

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

