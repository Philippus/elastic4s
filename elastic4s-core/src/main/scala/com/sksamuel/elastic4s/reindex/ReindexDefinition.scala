package com.sksamuel.elastic4s.reindex

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.elastic4s.{Index, Indexes, RefreshPolicy}
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration.FiniteDuration

case class ReindexDefinition(sourceIndexes: Indexes,
                             targetIndex: Index,
                             targetType: Option[String] = None,
                             filter: Option[QueryDefinition] = None,
                             requestsPerSecond: Option[Float] = None,
                             refresh: Option[RefreshPolicy] = None,
                             maxRetries: Option[Int] = None,
                             waitForCompletion: Option[Boolean] = None,
                             waitForActiveShards: Option[Int] = None,
                             timeout: Option[FiniteDuration] = None,
                             retryBackoffInitialTime: Option[FiniteDuration] = None,
                             shouldStoreResult: Option[Boolean] = None,
                             remoteHost: Option[String] = None,
                             remoteUser: Option[String] = None,
                             remotePass: Option[String] = None,
                             remoteSocketTimeout: Option[String] = None,
                             size: Option[Int] = None,
                             script: Option[ScriptDefinition] = None) {

  def remote(uri: String): ReindexDefinition = copy(remoteHost = Option(uri))
  def remote(uri: String, user: String, pass: String): ReindexDefinition =
    copy(remoteHost = Option(uri), remoteUser = Option(user), remotePass = Option(pass))

  def timeout(timeout: FiniteDuration): ReindexDefinition = copy(timeout = timeout.some)

  def refresh(refresh: RefreshPolicy): ReindexDefinition = copy(refresh = refresh.some)

  def filter(filter: QueryDefinition): ReindexDefinition = copy(filter = filter.some)

  def requestsPerSecond(requestsPerSecond: Float): ReindexDefinition =
    copy(requestsPerSecond = requestsPerSecond.some)

  def maxRetries(maxRetries: Int): ReindexDefinition = copy(maxRetries = maxRetries.some)

  def waitForActiveShards(count: Int): ReindexDefinition =
    copy(waitForActiveShards = count.some)

  def waitForCompletion(waitForCompletion: Boolean): ReindexDefinition =
    copy(waitForCompletion = waitForCompletion.some)

  def retryBackoffInitialTime(retryBackoffInitialTime: FiniteDuration): ReindexDefinition =
    copy(retryBackoffInitialTime = retryBackoffInitialTime.some)

  def size(size: Int): ReindexDefinition = copy(size = size.some)

  def shouldStoreResult(shouldStoreResult: Boolean): ReindexDefinition =
    copy(shouldStoreResult = shouldStoreResult.some)

  def script(script: ScriptDefinition): ReindexDefinition = copy(script = script.some)
}
