package com.sksamuel.elastic4s.http.search.queries.nested

import com.sksamuel.elastic4s.http.search.HighlightFieldBuilderFn
import com.sksamuel.elastic4s.http.search.queries.SortContentBuilder
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.InnerHitDefinition

object InnerHitQueryBodyFn {

  def apply(d: InnerHitDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    d.from.foreach(builder.field("from", _))
    d.explain.foreach(builder.field("explain", _))

    // source filtering
    d.fetchSource foreach { context =>
      if (context.fetchSource) {
        if (context.includes.nonEmpty || context.excludes.nonEmpty) {
          builder.startObject("_source")
          builder.array("includes", context.includes)
          builder.array("excludes", context.excludes)
          builder.endObject()
        }
      } else {
        builder.field("_source", false)
      }
    }

    d.trackScores.foreach(builder.field("track_scores", _))
    d.version.foreach(builder.field("version", _))
    d.size.foreach(builder.field("size", _))
    if (d.docValueFields.nonEmpty) {
      builder.array("docvalue_fields", d.docValueFields.toArray)
    }
    if (d.sorts.nonEmpty) {
      builder.startArray("sort")
      d.sorts.foreach { sort =>
        builder.rawValue(SortContentBuilder(sort))
      }
      builder.endArray()
    }
    if (d.storedFieldNames.nonEmpty) {
      builder.array("stored_fields", d.storedFieldNames.toArray)
    }
    if (d.highlights.nonEmpty) {
      builder.startObject("highlight")
      builder.startObject("fields")
      d.highlights.foreach { field =>
        builder.rawField(field.field, HighlightFieldBuilderFn(field))
      }
      builder.endObject()
      builder.endObject()
    }
    builder.endObject()
  }
}
