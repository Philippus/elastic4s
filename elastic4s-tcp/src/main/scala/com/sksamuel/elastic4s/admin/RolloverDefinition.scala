package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.mappings.MappingDefinition
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.action.admin.indices.rollover.RolloverRequestBuilder
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.unit.TimeValue

import scala.concurrent.duration.FiniteDuration

case class RolloverDefinition(sourceAlias: String,
                              maxIndexAgeCondition: Option[FiniteDuration] = None,
                              maxIndexDocsCondition: Option[Long] = None,
                              dryRun: Option[Boolean] = None,
                              mappings: Seq[MappingDefinition] = Nil,
                              newIndexName: Option[String] = None,
                              settings: Option[Settings] = None,
                              waitForActiveShards: Option[Int] = None,
                              masterNodeTimeout: Option[FiniteDuration] = None
                             ) {

  def populate(builder: RolloverRequestBuilder): Unit = {
    maxIndexAgeCondition.map(_.toNanos).map(TimeValue.timeValueNanos).foreach(builder.addMaxIndexAgeCondition)
    maxIndexDocsCondition.foreach(builder.addMaxIndexDocsCondition)
    dryRun.foreach(builder.dryRun)
    mappings.foreach { mapping => builder.mapping(mapping.`type`, mapping.buildWithName.string) }
    newIndexName.foreach(builder.setNewIndexName)
    settings.foreach(builder.settings)
    waitForActiveShards.foreach(builder.waitForActiveShards)
    masterNodeTimeout.map(_.toNanos).map(TimeValue.timeValueNanos).foreach(builder.setMasterNodeTimeout)
  }

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

  def settings(settings: Settings): RolloverDefinition = copy(settings = settings.some)
}
