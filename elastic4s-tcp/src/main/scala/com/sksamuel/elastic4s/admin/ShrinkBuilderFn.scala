package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.EnumConversions
import org.elasticsearch.action.admin.indices.shrink.ShrinkRequestBuilder
import org.elasticsearch.client.Client

object ShrinkBuilderFn {
  def apply(client: Client, req: ShrinkDefinition): ShrinkRequestBuilder = {
    val builder = client.admin().indices().prepareShrinkIndex(req.source, req.target)
    builder.setSettings(EnumConversions.settings(req.settings))
    req.waitForActiveShards.foreach(builder.setWaitForActiveShards)
    builder
  }
}
