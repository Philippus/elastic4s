package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.requests.admin.UpdateIndexLevelSettingsRequest
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object UpdateIndexLevelSettingsBuilder {

  def apply(d: UpdateIndexLevelSettingsRequest): XContentBuilder = {
    val source = XContentFactory.jsonBuilder().startObject("index")

    d.numberOfReplicas.foreach(source.field("number_of_replicas", _))
    d.autoExpandReplicas.foreach(source.field("auto_expand_replicas", _))
    d.refreshInterval.foreach(source.field("refresh_interval", _))
    d.maxResultWindow.foreach(source.field("max_result_window", _))

    source.endObject().endObject()
    source
  }

}
