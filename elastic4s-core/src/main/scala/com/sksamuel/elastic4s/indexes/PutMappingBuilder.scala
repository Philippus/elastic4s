package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.mappings.{MappingContentBuilder, PutMappingDefinition}
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object PutMappingBuilder {

  def apply(pm: PutMappingDefinition): XContentBuilder = {
    pm.rawSource.fold ({
      MappingContentBuilder.build(pm)
    })({ raw =>
      XContentFactory.jsonBuilder().rawValue(new BytesArray(raw))
    })
  }

}
