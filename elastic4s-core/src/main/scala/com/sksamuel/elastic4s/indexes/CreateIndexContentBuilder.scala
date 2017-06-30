package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.mappings.MappingBuilderFn

object CreateIndexContentBuilder {

  def apply(d: CreateIndexDefinition): XContentBuilder = {
    if (d.rawSource.isDefined) {
      XContentFactory.parse(d.rawSource.get)
    } else {
      val source = XContentFactory.jsonBuilder()

      if (d.settings.settings.nonEmpty || d.analysis.nonEmpty) {
        source.startObject("settings")

        if (d.settings.settings.nonEmpty) {
          source.startObject("index")

          d.settings.settings foreach {
            case (key, value) =>
              source.autofield(key, value)
          }

          source.endObject()
        }

        d.analysis.foreach(AnalysisBuilderFn.build(_, source))

        source.endObject() // end settings
      }

      if (d.mappings.nonEmpty) {
        source.startObject("mappings")
        for (mapping <- d.mappings) {
          source.startObject(mapping.`type`)
          MappingBuilderFn.build(mapping, source)
          source.endObject()
        }
        source.endObject()
      }

      source.endObject()
      source
    }
  }
}
