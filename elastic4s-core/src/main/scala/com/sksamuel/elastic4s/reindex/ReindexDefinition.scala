package com.sksamuel.elastic4s.reindex

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.elastic4s.{Indexes, URLParameters}
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy

import scala.concurrent.duration.FiniteDuration

case class ReindexDefinition(sourceIndexes: Indexes,
                             targetIndex: String,
                             targetType: Option[String] = None,
                             filter: Option[QueryDefinition] = None,
                             maxRetries: Option[Int] = None,
                             retryBackoffInitialTime: Option[FiniteDuration] = None,
                             shouldStoreResult: Option[Boolean] = None,
                             size: Option[Int] = None,
                             script: Option[ScriptDefinition] = None,
                             urlParams: Option[URLParameters] = None
                            ) {

  private def getURLParams = urlParams.getOrElse(URLParameters())

  def timeout(timeout: FiniteDuration): ReindexDefinition =
    copy(urlParams = getURLParams.timeout(timeout).some)

  def refresh(refresh: RefreshPolicy): ReindexDefinition =
    copy(urlParams = getURLParams.refresh(refresh).some)

  def requestsPerSecond(requestsPerSecond: Float): ReindexDefinition =
    copy(urlParams = getURLParams.requestsPerSecond(requestsPerSecond).some)

  def waitForActiveShards(waitForActiveShards: Int): ReindexDefinition =
    copy(urlParams = getURLParams.waitForActiveShards(waitForActiveShards).some)

  def waitForCompletion(waitForCompletion: Boolean): ReindexDefinition =
    copy(urlParams = getURLParams.waitForCompletion(waitForCompletion).some)

  def urlParams(urlParams: URLParameters): ReindexDefinition = copy(urlParams = urlParams.some)

  def filter(filter: QueryDefinition): ReindexDefinition = copy(filter = filter.some)

  def maxRetries(maxRetries: Int): ReindexDefinition = copy(maxRetries = maxRetries.some)

  def retryBackoffInitialTime(retryBackoffInitialTime: FiniteDuration): ReindexDefinition =
    copy(retryBackoffInitialTime = retryBackoffInitialTime.some)

  def size(size: Int): ReindexDefinition = copy(size = size.some)

  def shouldStoreResult(shouldStoreResult: Boolean): ReindexDefinition =
    copy(shouldStoreResult = shouldStoreResult.some)

  def script(script: ScriptDefinition): ReindexDefinition = copy(script = script.some)
}
