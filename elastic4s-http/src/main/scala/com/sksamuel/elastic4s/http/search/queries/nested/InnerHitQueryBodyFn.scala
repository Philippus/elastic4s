package com.sksamuel.elastic4s.http.search.queries.nested

import com.sksamuel.elastic4s.http.FetchSourceContextBuilderFn
import com.sksamuel.elastic4s.http.search.HighlightBuilderFn
import com.sksamuel.elastic4s.http.search.queries.SortBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.InnerHit

object InnerHitQueryBodyFn {

  def apply(d: InnerHit): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    if (d.name.trim.nonEmpty)
      builder.field("name", d.name)
    d.from.foreach(builder.field("from", _))
    d.explain.foreach(builder.field("explain", _))

    // source filtering
    d.fetchSource.foreach(FetchSourceContextBuilderFn(builder, _))

    d.trackScores.foreach(builder.field("track_scores", _))
    d.version.foreach(builder.field("version", _))
    d.size.foreach(builder.field("size", _))
    if (d.docValueFields.nonEmpty)
      builder.array("docvalue_fields", d.docValueFields.toArray)
    if (d.sorts.nonEmpty) {
      builder.startArray("sort")
      d.sorts.foreach { sort =>
        builder.rawValue(SortBuilderFn(sort))
      }
      builder.endArray()
    }
    if (d.storedFieldNames.nonEmpty)
      builder.array("stored_fields", d.storedFieldNames.toArray)

    d.highlight.foreach { highlight =>
      builder.rawField("highlight", HighlightBuilderFn(highlight))
    }
    builder.endObject()
  }
}
