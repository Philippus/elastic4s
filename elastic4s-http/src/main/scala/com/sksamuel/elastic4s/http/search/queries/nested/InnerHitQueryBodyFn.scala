package com.sksamuel.elastic4s.http.search.queries.nested

import java.util

import com.sksamuel.elastic4s.http.search.queries.{QueryBuilderFn, SortContentBuilder}
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
      builder.startObject("highlight")
      builder.startObject("fields")
      d.highlights.foreach { field =>
        builder.startObject(field.field)
        field.highlighterType.foreach(builder.field("type", _))
        field.boundaryChars.foreach(builder.field("boundary_chars", _))
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
    builder.endObject()
    builder
  }
}
