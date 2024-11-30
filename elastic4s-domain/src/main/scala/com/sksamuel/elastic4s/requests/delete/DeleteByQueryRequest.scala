package com.sksamuel.elastic4s.requests.delete

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.ext.OptionImplicits._
import com.sksamuel.elastic4s.requests.common.{RefreshPolicy, Slice, Slicing}
import com.sksamuel.elastic4s.requests.searches.queries.Query
import scala.concurrent.duration.FiniteDuration

case class DeleteByQueryRequest(
    indexes: Indexes,
    query: Query,
    requestsPerSecond: Option[Float] = None,
    maxRetries: Option[Int] = None,
    proceedOnConflicts: Option[Boolean] = None,
    refresh: Option[RefreshPolicy] = None,
    waitForActiveShards: Option[Int] = None,
    waitForCompletion: Option[Boolean] = None,
    retryBackoffInitialTime: Option[FiniteDuration] = None,
    timeout: Option[FiniteDuration] = None,
    scrollSize: Option[Int] = None,
    routing: Option[String] = None,
    shouldStoreResult: Option[Boolean] = None,
    maxDocs: Option[Int] = None,
    slices: Option[Int] = None,
    slice: Option[Slice] = None,
    ignoreUnavailable: Option[Boolean] = None
) {

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

  def waitForCompletion(waitForCompletion: Boolean): DeleteByQueryRequest =
    copy(waitForCompletion = waitForCompletion.some)

  def routing(r: String): DeleteByQueryRequest                                               = copy(routing = r.some)
  def retryBackoffInitialTime(retryBackoffInitialTime: FiniteDuration): DeleteByQueryRequest =
    copy(retryBackoffInitialTime = retryBackoffInitialTime.some)

  def timeout(timeout: FiniteDuration): DeleteByQueryRequest = copy(timeout = timeout.some)
  def maxDocs(maxDocs: Int): DeleteByQueryRequest            = copy(maxDocs = maxDocs.some)

  def shouldStoreResult(shouldStoreResult: Boolean): DeleteByQueryRequest =
    copy(shouldStoreResult = shouldStoreResult.some)

  def slices(slices: Int): DeleteByQueryRequest = copy(slices = slices.some)
  def automaticSlicing(): DeleteByQueryRequest  = copy(slices = Some(Slicing.AutoSlices))

  def slice(slice: Slice): DeleteByQueryRequest = copy(slice = slice.some)

  def ignoreUnavailable(ignoreUnavailable: Boolean): DeleteByQueryRequest =
    copy(ignoreUnavailable = ignoreUnavailable.some)
}
