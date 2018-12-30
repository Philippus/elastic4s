package com.sksamuel.elastic4s.requests.admin

import com.sksamuel.elastic4s.requests.mappings.MappingDefinition
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration.FiniteDuration

case class RolloverIndexRequest(sourceAlias: String,
                                maxAge: Option[String] = None,
                                maxDocs: Option[Long] = None,
                                maxSize: Option[String] = None,
                                dryRun: Option[Boolean] = None,
                                mappings: Seq[MappingDefinition] = Nil,
                                newIndexName: Option[String] = None,
                                settings: Map[String, Any] = Map.empty,
                                waitForActiveShards: Option[Int] = None,
                                masterNodeTimeout: Option[FiniteDuration] = None) {

  def waitForActiveShards(waitForActiveShards: Int): RolloverIndexRequest =
    copy(waitForActiveShards = waitForActiveShards.some)

  def masterNodeTimeout(masterNodeTimeout: FiniteDuration): RolloverIndexRequest =
    copy(masterNodeTimeout = masterNodeTimeout.some)

  def newIndexName(newIndexName: String): RolloverIndexRequest = copy(newIndexName = newIndexName.some)

  def maxSize(maxSize: String): RolloverIndexRequest = copy(maxSize = maxSize.some)

  def maxDocs(maxDocs: Long): RolloverIndexRequest = maxIndexDocsCondition(maxDocs)
  @deprecated("use maxDocs", "6.1.2")
  def maxIndexDocsCondition(maxDocs: Long): RolloverIndexRequest = copy(maxDocs = maxDocs.some)

  def maxAge(maxAge: String): RolloverIndexRequest = copy(maxAge = maxAge.some)
  @deprecated("use maxDocs", "6.1.2")
  def maxIndexAgeCondition(_maxAge: String): RolloverIndexRequest = maxAge(_maxAge)

  def dryRun(dryRun: Boolean): RolloverIndexRequest = copy(dryRun = dryRun.some)

  def mappings(mappings: Iterable[MappingDefinition]): RolloverIndexRequest = copy(mappings = mappings.toSeq)

  def settings(settings: Map[String, Any]): RolloverIndexRequest = copy(settings = settings)
}
