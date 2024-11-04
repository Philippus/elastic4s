package com.sksamuel.elastic4s.requests.update

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.ext.OptionImplicits._
import com.sksamuel.elastic4s.requests.admin.IndicesOptionsRequest
import com.sksamuel.elastic4s.requests.common.{RefreshPolicy, Slice, Slicing}
import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.requests.searches.queries.Query
import scala.concurrent.duration.FiniteDuration

case class UpdateByQueryAsyncRequest(indexes: Indexes,
                                     query: Query,
                                     requestsPerSecond: Option[Float] = None,
                                     maxRetries: Option[Int] = None,
                                     proceedOnConflicts: Option[Boolean] = None,
                                     pipeline: Option[String] = None,
                                     refresh: Option[RefreshPolicy] = None,
                                     script: Option[Script] = None,
                                     waitForActiveShards: Option[Int] = None,
                                     retryBackoffInitialTime: Option[FiniteDuration] = None,
                                     scroll: Option[String] = None,
                                     scrollSize: Option[Int] = None,
                                     slices: Option[Int] = None,
                                     slice: Option[Slice] = None,
                                     timeout: Option[FiniteDuration] = None,
                                     shouldStoreResult: Option[Boolean] = None,
                                     size: Option[Int] = None,
                                     indicesOptions: Option[IndicesOptionsRequest] = None) extends BaseUpdateByQueryRequest {

  def proceedOnConflicts(proceedOnConflicts: Boolean): UpdateByQueryAsyncRequest =
    copy(proceedOnConflicts = proceedOnConflicts.some)

  def refresh(refresh: RefreshPolicy): UpdateByQueryAsyncRequest = {
    if (refresh == RefreshPolicy.WAIT_FOR) throw new UnsupportedOperationException("Update by query does not support RefreshPolicy.WAIT_FOR")
    copy(refresh = refresh.some)
  }
  def refreshImmediately: UpdateByQueryAsyncRequest = refresh(RefreshPolicy.IMMEDIATE)

  def scroll(scroll: String): UpdateByQueryAsyncRequest = copy(scroll = scroll.some)
  def scroll(duration: FiniteDuration): UpdateByQueryAsyncRequest = copy(scroll = Some(duration.toSeconds + "s"))

  def scrollSize(scrollSize: Int): UpdateByQueryAsyncRequest = copy(scrollSize = scrollSize.some)
  def slice(slice: Slice): UpdateByQueryAsyncRequest = copy(slice = slice.some)
  def slices(slices: Int): UpdateByQueryAsyncRequest = copy(slices = slices.some)
  def automaticSlicing(): UpdateByQueryAsyncRequest = copy(slices = Some(Slicing.AutoSlices))

  def requestsPerSecond(requestsPerSecond: Float): UpdateByQueryAsyncRequest =
    copy(requestsPerSecond = requestsPerSecond.some)

  def maxRetries(maxRetries: Int): UpdateByQueryAsyncRequest = copy(maxRetries = maxRetries.some)

  def waitForActiveShards(waitForActiveShards: Int): UpdateByQueryAsyncRequest =
    copy(waitForActiveShards = waitForActiveShards.some)

  def retryBackoffInitialTime(retryBackoffInitialTime: FiniteDuration): UpdateByQueryAsyncRequest =
    copy(retryBackoffInitialTime = retryBackoffInitialTime.some)

  def timeout(timeout: FiniteDuration): UpdateByQueryAsyncRequest = copy(timeout = timeout.some)

  def size(size: Int): UpdateByQueryAsyncRequest = copy(size = size.some)

  def script(script: Script): UpdateByQueryAsyncRequest = copy(script = script.some)
  def script(source: String): UpdateByQueryAsyncRequest = script(Script(source))

  def shouldStoreResult(shouldStoreResult: Boolean): UpdateByQueryAsyncRequest =
    copy(shouldStoreResult = shouldStoreResult.some)

  def indicesOptions(options: IndicesOptionsRequest): UpdateByQueryAsyncRequest = copy(indicesOptions = options.some)

  override val waitForCompletion: Option[Boolean] = Some(false)
}
