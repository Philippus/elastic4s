package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.mappings.MappingBuilderFn
import org.elasticsearch.action.admin.indices.rollover.RolloverRequestBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.TimeValue

object RolloverBuilderFn {

  import com.sksamuel.elastic4s.EnumConversions._

  def apply(client: Client, req: Rollover): RolloverRequestBuilder = {
    val builder = client.admin().indices().prepareRolloverIndex(req.sourceAlias)
    req.maxAge.map(TimeValue.parseTimeValue(_, "maxAge")).foreach(builder.addMaxIndexAgeCondition)
    req.maxDocs.foreach(builder.addMaxIndexDocsCondition)
    req.dryRun.foreach(builder.dryRun)
    req.mappings.foreach { mapping => builder.mapping(mapping.`type`, MappingBuilderFn.buildWithName(mapping, mapping.`type`).string) }
    req.newIndexName.foreach(builder.setNewIndexName)
    if (req.settings.nonEmpty)
      builder.settings(req.settings)
    req.waitForActiveShards.foreach(builder.waitForActiveShards)
    req.masterNodeTimeout.map(_.toNanos).map(TimeValue.timeValueNanos).foreach(builder.setMasterNodeTimeout)
    builder
  }
}
