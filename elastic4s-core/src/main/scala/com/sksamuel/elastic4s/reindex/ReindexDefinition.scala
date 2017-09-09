package com.sksamuel.elastic4s.reindex

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.elastic4s.{AbstractURLParameterDefinition, Indexes}
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy

import scala.concurrent.duration.FiniteDuration

case class ReindexDefinition(sourceIndexes: Indexes,
                             targetIndex: String,
                             targetType: Option[String] = None,
                             filter: Option[QueryDefinition] = None,
                             override val requestsPerSecond: Option[Float] = None,
                             override val refresh: Option[RefreshPolicy] = None,
                             maxRetries: Option[Int] = None,
                             override val waitForActiveShards: Option[Int] = None,
                             override val timeout: Option[FiniteDuration] = None,
                             retryBackoffInitialTime: Option[FiniteDuration] = None,
                             shouldStoreResult: Option[Boolean] = None,
                             size: Option[Int] = None,
                             script: Option[ScriptDefinition] = None,
                             override val waitForCompletion: Option[Boolean] = None)
  extends AbstractURLParameterDefinition {

  def timeout(timeout: FiniteDuration): ReindexDefinition = copy(timeout = timeout.some)

  def refresh(refresh: RefreshPolicy): ReindexDefinition = copy(refresh = refresh.some)

  def filter(filter: QueryDefinition): ReindexDefinition = copy(filter = filter.some)

  def requestsPerSecond(requestsPerSecond: Float): ReindexDefinition =
    copy(requestsPerSecond = requestsPerSecond.some)

  def maxRetries(maxRetries: Int): ReindexDefinition = copy(maxRetries = maxRetries.some)

  def waitForActiveShards(waitForActiveShards: Int): ReindexDefinition =
    copy(waitForActiveShards = waitForActiveShards.some)

  def waitForCompletion(waitForCompletion: Boolean): ReindexDefinition =
    copy(waitForCompletion = waitForCompletion.some)

  def retryBackoffInitialTime(retryBackoffInitialTime: FiniteDuration): ReindexDefinition =
    copy(retryBackoffInitialTime = retryBackoffInitialTime.some)

  def size(size: Int): ReindexDefinition = copy(size = size.some)

  def shouldStoreResult(shouldStoreResult: Boolean): ReindexDefinition =
    copy(shouldStoreResult = shouldStoreResult.some)

  def script(script: ScriptDefinition): ReindexDefinition = copy(script = script.some)
}
