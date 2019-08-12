package com.sksamuel.elastic4s.requests.delete

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration.FiniteDuration

case class DeleteByQueryRequest(indexes: Indexes,
                                query: Query,
                                requestsPerSecond: Option[Float] = None,
                                maxRetries: Option[Int] = None,
                                proceedOnConflicts: Option[Boolean] = None,
                                refresh: Option[RefreshPolicy] = None,
                                waitForActiveShards: Option[Int] = None,
                                retryBackoffInitialTime: Option[FiniteDuration] = None,
                                timeout: Option[FiniteDuration] = None,
                                scrollSize: Option[Int] = None,
                                routing: Option[String] = None,
                                shouldStoreResult: Option[Boolean] = None,
                                size: Option[Int] = None) {

  def proceedOnConflicts(proceedOnConflicts: Boolean): DeleteByQueryRequest =
    copy(proceedOnConflicts = proceedOnConflicts.some)

  def refresh(refresh: RefreshPolicy): DeleteByQueryRequest = copy(refresh = refresh.some)
  def refreshImmediately: DeleteByQueryRequest              = refresh(RefreshPolicy.IMMEDIATE)

  def scrollSize(scrollSize: Int): DeleteByQueryRequest = copy(scrollSize = scrollSize.some)

  def requestsPerSecond(requestsPerSecond: Float): DeleteByQueryRequest =
    copy(requestsPerSecond = requestsPerSecond.some)

  def maxRetries(maxRetries: Int): DeleteByQueryRequest = copy(maxRetries = maxRetries.some)

  def waitForActiveShards(waitForActiveShards: Int): DeleteByQueryRequest =
    copy(waitForActiveShards = waitForActiveShards.some)

  def routing(r: String): DeleteByQueryRequest   = copy(routing = r.some)
  def retryBackoffInitialTime(retryBackoffInitialTime: FiniteDuration): DeleteByQueryRequest =
    copy(retryBackoffInitialTime = retryBackoffInitialTime.some)

  def timeout(timeout: FiniteDuration): DeleteByQueryRequest = copy(timeout = timeout.some)
  def size(size: Int): DeleteByQueryRequest = copy(size = size.some)

  def shouldStoreResult(shouldStoreResult: Boolean): DeleteByQueryRequest =
    copy(shouldStoreResult = shouldStoreResult.some)

}
