package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.admin.UpdateIndexLevelSettingsDefinition
import com.sksamuel.elastic4s.mappings.PutMappingDefinition
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object PutMappingBuilder {

  def apply(pm: PutMappingDefinition): XContentBuilder = {
    pm.rawSource.fold ({
      val source = XContentFactory.jsonBuilder().startObject()









      //    d.numberOfReplicas.foreach(source.field("number_of_replicas", _))
      //    d.autoExpandReplicas.foreach(source.field("auto_expand_replicas", _))
      //    d.refreshInterval.foreach(source.field("refresh_interval", _))
      //    d.maxResultWindow.foreach(source.field("max_result_window", _))

      source.endObject()
      source
    })({ raw =>
      XContentFactory.jsonBuilder().rawValue(new BytesArray(raw))
    })
  }

}
