package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.mappings.{MappingBuilderFn, PutMappingDefinition}

object PutMappingBuilder {

  def apply(pm: PutMappingDefinition): XContentBuilder = {
    pm.rawSource.fold ({
      MappingBuilderFn.build(pm)
    })({ raw =>
      XContentFactory.jsonBuilder().rawValue(raw)
    })
  }

}
