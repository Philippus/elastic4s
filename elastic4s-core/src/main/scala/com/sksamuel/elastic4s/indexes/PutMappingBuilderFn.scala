package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.mappings.{MappingBuilderFn, PutMappingDefinition}

object PutMappingBuilderFn {

  def apply(pm: PutMappingDefinition): XContentBuilder = {
    pm.rawSource match {
      case None => MappingBuilderFn.build(pm)
      case Some(source) => XContentFactory.parse(source)
    }
  }
}
