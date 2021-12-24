package com.sksamuel.elastic4s.handlers.index

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.admin.UpdateIndexLevelSettingsRequest

object UpdateIndexLevelSettingsBuilder {

  def apply(d: UpdateIndexLevelSettingsRequest): XContentBuilder = {
    val source = XContentFactory.jsonBuilder().startObject("index")

    d.numberOfReplicas.foreach(source.field("number_of_replicas", _))
    d.autoExpandReplicas.foreach(source.field("auto_expand_replicas", _))
    d.refreshInterval.foreach(source.field("refresh_interval", _))
    d.maxResultWindow.foreach(source.field("max_result_window", _))

    d.translog.foreach { t =>
      val translog = source.startObject("translog")
      translog.field("durability", t.durability)
      t.syncInterval.foreach(si => translog.field("sync_interval", si))
      t.flushThresholdSize.foreach(si => translog.field("flush_threshold_size", si))
      translog.endObject()
    }

    source.endObject().endObject()
    source
  }

}
