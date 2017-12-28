package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.mappings.MappingDefinition
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration.FiniteDuration

case class Rollover(sourceAlias: String,
                    maxAge: Option[String] = None,
                    maxDocs: Option[Long] = None,
                    maxSize: Option[String] = None,
                    dryRun: Option[Boolean] = None,
                    mappings: Seq[MappingDefinition] = Nil,
                    newIndexName: Option[String] = None,
                    settings: Map[String, Any] = Map.empty,
                    waitForActiveShards: Option[Int] = None,
                    masterNodeTimeout: Option[FiniteDuration] = None) {

  def waitForActiveShards(waitForActiveShards: Int): Rollover =
    copy(waitForActiveShards = waitForActiveShards.some)

  def masterNodeTimeout(masterNodeTimeout: FiniteDuration): Rollover =
    copy(masterNodeTimeout = masterNodeTimeout.some)

  def newIndexName(newIndexName: String): Rollover = copy(newIndexName = newIndexName.some)

  def maxSize(maxSize: String): Rollover = copy(maxSize = maxSize.some)

  def maxDocs(maxDocs: Long): Rollover = maxIndexDocsCondition(maxDocs)
  @deprecated("use maxDocs", "6.1.2")
  def maxIndexDocsCondition(maxDocs: Long): Rollover = copy(maxDocs = maxDocs.some)

  def maxAge(maxAge: String): Rollover = copy(maxAge = maxAge.some)
  @deprecated("use maxDocs", "6.1.2")
  def maxIndexAgeCondition(_maxAge: String): Rollover = maxAge(_maxAge)

  def dryRun(dryRun: Boolean): Rollover = copy(dryRun = dryRun.some)

  def mappings(mappings: Iterable[MappingDefinition]): Rollover = copy(mappings = mappings.toSeq)

  def settings(settings: Map[String, Any]): Rollover = copy(settings = settings)
}
