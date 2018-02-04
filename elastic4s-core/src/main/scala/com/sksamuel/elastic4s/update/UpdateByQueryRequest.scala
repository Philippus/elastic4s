package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.script.Script
import com.sksamuel.elastic4s.searches.queries.Query
import com.sksamuel.elastic4s.{Indexes, IndexesAndTypes, RefreshPolicy}
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration.FiniteDuration

case class UpdateByQueryRequest(indexesAndTypes: IndexesAndTypes,
                                query: Query,
                                requestsPerSecond: Option[Float] = None,
                                maxRetries: Option[Int] = None,
                                proceedOnConflicts: Option[Boolean] = None,
                                pipeline: Option[String] = None,
                                refresh: Option[RefreshPolicy] = None,
                                script: Option[Script] = None,
                                waitForActiveShards: Option[Int] = None,
                                retryBackoffInitialTime: Option[FiniteDuration] = None,
                                scrollSize: Option[Int] = None,
                                timeout: Option[FiniteDuration] = None,
                                shouldStoreResult: Option[Boolean] = None,
                                size: Option[Int] = None) {

  def proceedOnConflicts(proceedOnConflicts: Boolean): UpdateByQueryRequest =
    copy(proceedOnConflicts = proceedOnConflicts.some)

  @deprecated("use proceedOnConflicts")
  def abortOnVersionConflict(abortOnVersionConflict: Boolean): UpdateByQueryRequest =
    proceedOnConflicts(abortOnVersionConflict)

  def refresh(refresh: RefreshPolicy): UpdateByQueryRequest = copy(refresh = refresh.some)
  def refreshImmediately: UpdateByQueryRequest              = refresh(RefreshPolicy.IMMEDIATE)

  def scrollSize(scrollSize: Int): UpdateByQueryRequest = copy(scrollSize = scrollSize.some)

  def requestsPerSecond(requestsPerSecond: Float): UpdateByQueryRequest =
    copy(requestsPerSecond = requestsPerSecond.some)

  def maxRetries(maxRetries: Int): UpdateByQueryRequest = copy(maxRetries = maxRetries.some)

  def waitForActiveShards(waitForActiveShards: Int): UpdateByQueryRequest =
    copy(waitForActiveShards = waitForActiveShards.some)

  def retryBackoffInitialTime(retryBackoffInitialTime: FiniteDuration): UpdateByQueryRequest =
    copy(retryBackoffInitialTime = retryBackoffInitialTime.some)

  def timeout(timeout: FiniteDuration): UpdateByQueryRequest = copy(timeout = timeout.some)

  def size(size: Int): UpdateByQueryRequest = copy(size = size.some)

  def script(script: Script): UpdateByQueryRequest = copy(script = script.some)

  def shouldStoreResult(shouldStoreResult: Boolean): UpdateByQueryRequest =
    copy(shouldStoreResult = shouldStoreResult.some)

}

object UpdateByQueryRequest {
  def apply(indexes: Indexes, query: Query) = new UpdateByQueryRequest(indexes.toIndexesAndTypes, query)
}
