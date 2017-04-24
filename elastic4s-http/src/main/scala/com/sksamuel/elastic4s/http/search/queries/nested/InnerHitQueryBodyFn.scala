package com.sksamuel.elastic4s.http.search.queries.nested

import com.sksamuel.elastic4s.http.search.HighlightFieldBuilderFn
import com.sksamuel.elastic4s.searches.queries.InnerHitDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

import scala.collection.JavaConverters._

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
      builder.field("docvalue_fields", d.docValueFields.asJava)
    }
    if (d.sorts.nonEmpty) {
      builder.field("sort", d.sorts.asJava)
    }
    if (d.storedFieldNames.nonEmpty) {
      builder.field("stored_fields", d.storedFieldNames.asJava)
    }
    if (d.highlights.nonEmpty) {
      builder.rawField("highlight", HighlightFieldBuilderFn(d.highlights).bytes(), XContentType.JSON)
    }
    builder.endObject()
    builder
  }
}
