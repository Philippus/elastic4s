package com.sksamuel.elastic4s.requests.update

import com.sksamuel.elastic4s.requests.common.{RefreshPolicy, Slice}
import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.elastic4s.{Indexes, IndexesAndTypes}
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
                                waitForCompletion: Option[Boolean] = None,
                                retryBackoffInitialTime: Option[FiniteDuration] = None,
                                scroll: Option[String] = None,
                                scrollSize: Option[Int] = None,
                                slices: Option[Int] = None,
                                slice: Option[Slice] = None,
                                timeout: Option[FiniteDuration] = None,
                                shouldStoreResult: Option[Boolean] = None,
                                size: Option[Int] = None) {

  def proceedOnConflicts(proceedOnConflicts: Boolean): UpdateByQueryRequest =
    copy(proceedOnConflicts = proceedOnConflicts.some)

  @deprecated("use proceedOnConflicts", "6.2.0")
  def abortOnVersionConflict(abortOnVersionConflict: Boolean): UpdateByQueryRequest =
    proceedOnConflicts(abortOnVersionConflict)

  def refresh(refresh: RefreshPolicy): UpdateByQueryRequest = copy(refresh = refresh.some)
  def refreshImmediately: UpdateByQueryRequest              = refresh(RefreshPolicy.IMMEDIATE)

  def scroll(scroll: String): UpdateByQueryRequest = copy(scroll = scroll.some)
  def scroll(duration: FiniteDuration): UpdateByQueryRequest = copy(scroll = Some(duration.toSeconds + "s"))

  def scrollSize(scrollSize: Int): UpdateByQueryRequest = copy(scrollSize = scrollSize.some)
  def slice(slice: Slice): UpdateByQueryRequest         = copy(slice = slice.some)
  def slices(slices: Int): UpdateByQueryRequest         = copy(slices = slices.some)

  def requestsPerSecond(requestsPerSecond: Float): UpdateByQueryRequest =
    copy(requestsPerSecond = requestsPerSecond.some)

  def maxRetries(maxRetries: Int): UpdateByQueryRequest = copy(maxRetries = maxRetries.some)

  def waitForActiveShards(waitForActiveShards: Int): UpdateByQueryRequest =
    copy(waitForActiveShards = waitForActiveShards.some)

  def waitForCompletion(w: Boolean): UpdateByQueryRequest = copy(waitForCompletion = w.some)

  def retryBackoffInitialTime(retryBackoffInitialTime: FiniteDuration): UpdateByQueryRequest =
    copy(retryBackoffInitialTime = retryBackoffInitialTime.some)

  def timeout(timeout: FiniteDuration): UpdateByQueryRequest = copy(timeout = timeout.some)

  def size(size: Int): UpdateByQueryRequest = copy(size = size.some)

  def script(script: Script): UpdateByQueryRequest = copy(script = script.some)
  def script(source: String): UpdateByQueryRequest = script(Script(source))

  def shouldStoreResult(shouldStoreResult: Boolean): UpdateByQueryRequest =
    copy(shouldStoreResult = shouldStoreResult.some)

}

object UpdateByQueryRequest {
  def apply(indexes: Indexes, query: Query) = new UpdateByQueryRequest(indexes.toIndexesAndTypes, query)
}
