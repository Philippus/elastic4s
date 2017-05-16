package com.sksamuel.elastic4s.http.search.queries.nested

import com.sksamuel.elastic4s.http.search.HighlightFieldBuilderFn
import com.sksamuel.elastic4s.http.search.queries.SortContentBuilder
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.InnerHitDefinition

object InnerHitQueryBodyFn {

  def apply(d: InnerHitDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    d.from.foreach(builder.field("from", _))
    d.explain.foreach(builder.field("explain", _))
    d.fetchSource.foreach(builder.field("_source", _))
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
      builder.rawField("highlight", HighlightFieldBuilderFn(d.highlights))
    }
    builder.endObject()
    builder
  }
}
