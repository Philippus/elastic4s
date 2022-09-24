package com.sksamuel.elastic4s.handlers.index.mapping

import com.sksamuel.elastic4s.fields.ElasticField
import com.sksamuel.elastic4s.handlers.fields.ElasticFieldBuilderFn
import com.sksamuel.elastic4s.requests.indexes.IndexMappings

object IndexMappersToFields {

  def mappingToFields(indexMapping: IndexMappings): Seq[ElasticField] =
    indexMapping.mappings.map {
      case (name, value: Map[String, Any]) => ElasticFieldBuilderFn.construct(name, value)
    }.toSeq
}
