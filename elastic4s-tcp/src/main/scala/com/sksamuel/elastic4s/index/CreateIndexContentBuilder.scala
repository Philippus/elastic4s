package com.sksamuel.elastic4s.index

import com.sksamuel.elastic4s.indexes.{AnalysisBuilderFn, CreateIndexDefinition}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.mappings.MappingBuilderFn

object CreateIndexContentBuilder {

  def apply(d: CreateIndexDefinition): XContentBuilder = {
    if (d.rawSource.isDefined) {
      XContentFactory.parse(d.rawSource.get)
    } else {
      val builder = XContentFactory.jsonBuilder()

      if (d.settings.settings.nonEmpty || d.analysis.nonEmpty) {
        builder.startObject("settings")

        if (d.settings.settings.nonEmpty) {
          builder.startObject("index")

          d.settings.settings foreach {
            case (key, value) =>
              builder.autofield(key, value)
          }

          builder.endObject()
        }

        d.analysis.foreach(AnalysisBuilderFn.build(_, builder))

        builder.endObject() // end settings
      }

      if (d.mappings.nonEmpty) {
        builder.startObject("mappings")
        for (mapping <- d.mappings) {
          builder.rawField(mapping.`type`, MappingBuilderFn.build(mapping))
        }
        builder.endObject()
      }

      builder.endObject()
      builder
    }
  }
}
