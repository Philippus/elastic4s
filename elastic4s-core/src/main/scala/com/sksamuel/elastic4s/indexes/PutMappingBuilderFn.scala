package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.mappings.{MappingBuilderFn, PutMappingRequest}

object PutMappingBuilderFn {

  def apply(pm: PutMappingRequest): XContentBuilder =
    pm.rawSource match {
      case None         => MappingBuilderFn.build(pm)
      case Some(source) => XContentFactory.parse(source)
    }
}
