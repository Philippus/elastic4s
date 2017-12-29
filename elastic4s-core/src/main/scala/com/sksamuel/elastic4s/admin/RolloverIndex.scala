package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.mappings.MappingDefinition
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration.FiniteDuration

case class RolloverIndex(sourceAlias: String,
                         maxAge: Option[String] = None,
                         maxDocs: Option[Long] = None,
                         maxSize: Option[String] = None,
                         dryRun: Option[Boolean] = None,
                         mappings: Seq[MappingDefinition] = Nil,
                         newIndexName: Option[String] = None,
                         settings: Map[String, Any] = Map.empty,
                         waitForActiveShards: Option[Int] = None,
                         masterNodeTimeout: Option[FiniteDuration] = None) {

  def waitForActiveShards(waitForActiveShards: Int): RolloverIndex =
    copy(waitForActiveShards = waitForActiveShards.some)

  def masterNodeTimeout(masterNodeTimeout: FiniteDuration): RolloverIndex =
    copy(masterNodeTimeout = masterNodeTimeout.some)

  def newIndexName(newIndexName: String): RolloverIndex = copy(newIndexName = newIndexName.some)

  def maxSize(maxSize: String): RolloverIndex = copy(maxSize = maxSize.some)

  def maxDocs(maxDocs: Long): RolloverIndex = maxIndexDocsCondition(maxDocs)
  @deprecated("use maxDocs", "6.1.2")
  def maxIndexDocsCondition(maxDocs: Long): RolloverIndex = copy(maxDocs = maxDocs.some)

  def maxAge(maxAge: String): RolloverIndex = copy(maxAge = maxAge.some)
  @deprecated("use maxDocs", "6.1.2")
  def maxIndexAgeCondition(_maxAge: String): RolloverIndex = maxAge(_maxAge)

  def dryRun(dryRun: Boolean): RolloverIndex = copy(dryRun = dryRun.some)

  def mappings(mappings: Iterable[MappingDefinition]): RolloverIndex = copy(mappings = mappings.toSeq)

  def settings(settings: Map[String, Any]): RolloverIndex = copy(settings = settings)
}
