package com.sksamuel.elastic4s.http.index

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.indexes.{AnalysisBuilderFn, CreateIndexRequest}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.mappings.MappingBuilderFn

object CreateIndexContentBuilder {

  def apply(d: CreateIndexRequest): XContentBuilder =
    if (d.rawSource.isDefined)
      XContentFactory.parse(d.rawSource.get)
    else {
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
        for (mapping <- d.mappings)
          builder.rawField(mapping.`type`, MappingBuilderFn.build(mapping))
        builder.endObject()
      }

      if (d.aliases.nonEmpty) {
        builder.startObject("aliases")
        for (alias <- d.aliases) {
          builder.startObject(alias.name)
          alias.filter.map(QueryBuilderFn.apply).foreach(builder.rawField("filter", _))
          alias.routing.foreach(builder.field("routing", _))
          builder.endObject()
        }
        builder.endObject()
      }

      builder.endObject()
      builder
    }
}
