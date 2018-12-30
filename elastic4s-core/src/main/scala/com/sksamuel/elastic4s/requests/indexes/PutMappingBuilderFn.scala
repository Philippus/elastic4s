package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.requests.mappings.{MappingBuilderFn, PutMappingRequest}
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object PutMappingBuilderFn {

  def apply(pm: PutMappingRequest): XContentBuilder =
    pm.rawSource match {
      case None         => MappingBuilderFn.build(pm)
      case Some(source) => XContentFactory.parse(source)
    }
}
