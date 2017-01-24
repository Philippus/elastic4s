package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.mappings.MappingContentBuilder
import org.elasticsearch.action.admin.indices.rollover.RolloverRequestBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.TimeValue

object RolloverBuilderFn {
  def apply(client: Client, req: RolloverDefinition): RolloverRequestBuilder = {
    val builder = client.admin().indices().prepareRolloverIndex(req.sourceAlias)
    req.maxIndexAgeCondition.map(_.toNanos).map(TimeValue.timeValueNanos).foreach(builder.addMaxIndexAgeCondition)
    req.maxIndexDocsCondition.foreach(builder.addMaxIndexDocsCondition)
    req.dryRun.foreach(builder.dryRun)
    req.mappings.foreach { mapping => builder.mapping(mapping.`type`, MappingContentBuilder.buildWithName(mapping, mapping.`type`).string) }
    req.newIndexName.foreach(builder.setNewIndexName)
    req.settings.foreach(builder.settings)
    req.waitForActiveShards.foreach(builder.waitForActiveShards)
    req.masterNodeTimeout.map(_.toNanos).map(TimeValue.timeValueNanos).foreach(builder.setMasterNodeTimeout)
    builder
  }
}
