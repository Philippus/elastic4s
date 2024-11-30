package com.sksamuel.elastic4s.handlers.index.mapping

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.mappings.PutMappingRequest

object PutMappingBuilderFn {

  def apply(pm: PutMappingRequest): XContentBuilder =
    pm.rawSource match {
      case None         => MappingBuilderFn.build(pm)
      case Some(source) => XContentFactory.parse(source)
    }
}
