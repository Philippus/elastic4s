package com.sksamuel.elastic4s.admin

import org.elasticsearch.action.admin.indices.shrink.ShrinkRequestBuilder
import org.elasticsearch.client.Client

object ShrinkBuilderFn {
  def apply(client: Client, req: ShrinkDefinition): ShrinkRequestBuilder = {
    val builder = client.admin().indices().prepareShrinkIndex(req.source, req.target)
    builder.setSettings(req.settings)
    req.waitForActiveShards.foreach(builder.setWaitForActiveShards)
    builder
  }
}
