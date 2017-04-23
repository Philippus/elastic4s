package com.sksamuel.elastic4s.http.search

import java.util

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.searches.HighlightFieldDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

import scala.collection.JavaConverters._

object HighlightFieldBuilderFn {

  def apply(fields: Iterable[HighlightFieldDefinition]): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("fields")
    fields.foreach { field =>
      builder.startObject(field.field)
      field.boundaryChars.foreach(chars => builder.field("boundary_chars", String.valueOf(chars)))
      field.boundaryMaxScan.foreach(builder.field("boundary_max_scan", _))
      field.forceSource.foreach(builder.field("force_source", _))
      field.fragmentOffset.foreach(builder.field("fragment_offset", _))
      field.fragmentSize.foreach(builder.field("fragment_size", _))
      field.highlightQuery.map(QueryBuilderFn.apply).map(_.bytes()).foreach { highlight =>
        builder.rawField("highlight_query", highlight, XContentType.JSON)
      }
      if (field.matchedFields.nonEmpty) {
        builder.field("matched_fields", field.matchedFields.asJava)
      }
      field.highlighterType.foreach(builder.field("type", _))
      field.noMatchSize.foreach(builder.field("no_match_size", _))
      field.numOfFragments.foreach(builder.field("number_of_fragments", _))
      field.order.foreach(builder.field("order", _))
      field.phraseLimit.foreach(builder.field("phrase_limit", _))
      if (field.postTags.nonEmpty || field.preTags.nonEmpty) {
        if (field.postTags.isEmpty)
          builder.field("post_tags", util.Arrays.asList("</em>"))
        else
          builder.field("post_tags", field.postTags.asJava)

        if (field.preTags.isEmpty)
          builder.field("pre_tags", util.Arrays.asList("<em>"))
        else
          builder.field("pre_tags", field.preTags.asJava)
      }
      field.requireFieldMatch.foreach(builder.field("require_field_match", _))
      builder.endObject()
    }
    builder.endObject()
    builder.endObject()
  }
}
