package com.sksamuel.elastic4s.handlers.index

import com.sksamuel.elastic4s.analysis.AnalysisBuilder
import com.sksamuel.elastic4s.handlers.index.mapping.MappingBuilderFn
import com.sksamuel.elastic4s.handlers.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.indexes.CreateIndexRequest

object CreateIndexContentBuilder {

  def apply(d: CreateIndexRequest): XContentBuilder =
    if (d.rawSource.isDefined) {
      XContentFactory.parse(d.rawSource.get)
    } else {
      val builder = XContentFactory.jsonBuilder()

      if (d.settings.settings.nonEmpty || d._analysis.nonEmpty || d.analysis.nonEmpty) {
        builder.startObject("settings")

        if (d.settings.settings.nonEmpty) {
          builder.startObject("index")

          d.settings.settings foreach {
            case (key, value) =>
              builder.autofield(key, value)
          }

          builder.endObject()
        }

        d._analysis.foreach(AnalysisBuilderFn.build(_, builder))

        d.analysis.foreach { a => builder.rawField("analysis", AnalysisBuilder.build(a)) }

        builder.endObject() // end settings
      }

      d.mapping.foreach { mapping =>
        builder.rawField("mappings", MappingBuilderFn.build(mapping))
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
