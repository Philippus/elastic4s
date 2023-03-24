package com.sksamuel.elastic4s.handlers.index

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.admin.UpdateIndexLevelSettingsRequest

object UpdateIndexLevelSettingsBuilder {

  def apply(d: UpdateIndexLevelSettingsRequest): XContentBuilder = {
    val builder = XContentFactory.obj()

    val settings = builder.startObject("settings")
    for ((key, value) <- d.settings)
      settings.field(key, value)

    d.numberOfReplicas.foreach(settings.field("index.number_of_replicas", _))
    d.autoExpandReplicas.foreach(settings.field("index.auto_expand_replicas", _))
    d.refreshInterval.foreach(settings.field("index.refresh_interval", _))
    d.maxResultWindow.foreach(settings.field("index.max_result_window", _))

    d.translog.foreach { t =>
      settings.field("index.translog.durability", t.durability)
      t.syncInterval.foreach(si => settings.field("index.translog.sync_interval", si))
      t.flushThresholdSize.foreach(si => settings.field("index.translog.flush_threshold_size", si))
    }

    settings.endObject()
    builder.endObject()

    builder
  }

}
