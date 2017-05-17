package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.mappings.MappingDefinition
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration.FiniteDuration

case class RolloverDefinition(sourceAlias: String,
                              maxIndexAgeCondition: Option[FiniteDuration] = None,
                              maxIndexDocsCondition: Option[Long] = None,
                              dryRun: Option[Boolean] = None,
                              mappings: Seq[MappingDefinition] = Nil,
                              newIndexName: Option[String] = None,
                              settings: Map[String, Any] = Map.empty,
                              waitForActiveShards: Option[Int] = None,
                              masterNodeTimeout: Option[FiniteDuration] = None
                             ) {

  def waitForActiveShards(waitForActiveShards: Int): RolloverDefinition =
    copy(waitForActiveShards = waitForActiveShards.some)

  def masterNodeTimeout(masterNodeTimeout: FiniteDuration): RolloverDefinition =
    copy(masterNodeTimeout = masterNodeTimeout.some)

  def newIndexName(newIndexName: String): RolloverDefinition = copy(newIndexName = newIndexName.some)

  def maxIndexDocsCondition(maxIndexDocsCondition: Long): RolloverDefinition =
    copy(maxIndexDocsCondition = maxIndexDocsCondition.some)

  def maxIndexAgeCondition(maxIndexAgeCondition: FiniteDuration): RolloverDefinition =
    copy(maxIndexAgeCondition = maxIndexAgeCondition.some)

  def dryRun(dryRun: Boolean): RolloverDefinition = copy(dryRun = dryRun.some)

  def mappings(mappings: Iterable[MappingDefinition]): RolloverDefinition = copy(mappings = mappings.toSeq)

  def settings(settings: Map[String, Any]): RolloverDefinition = copy(settings = settings)
}
