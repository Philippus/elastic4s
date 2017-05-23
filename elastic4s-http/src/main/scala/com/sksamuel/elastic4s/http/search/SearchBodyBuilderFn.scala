package com.sksamuel.elastic4s.http.search

import com.sksamuel.elastic4s.http.EnumConversions
import com.sksamuel.elastic4s.http.search.aggs.AggregationBuilderFn
import com.sksamuel.elastic4s.http.search.collapse.CollapseBuilderFn
import com.sksamuel.elastic4s.http.search.queries.{QueryBuilderFn, SortContentBuilder}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.SearchDefinition
import com.sksamuel.elastic4s.searches.suggestion.TermSuggestionDefinition

object SearchBodyBuilderFn {

  def apply(request: SearchDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    request.query.map(QueryBuilderFn.apply).foreach(x => builder.rawField("query", x.string))
    request.postFilter.map(QueryBuilderFn.apply).foreach(x => builder.rawField("post_filter", x.string))
    request.collapse.map(CollapseBuilderFn.apply).foreach(x => builder.rawField("collapse", x.string))

    request.from.foreach(builder.field("from", _))
    request.size.foreach(builder.field("size", _))

    if (request.explain.contains(true)) {
      builder.field("explain", true)
    }

    request.minScore.foreach(builder.field("min_score", _))
    if (request.searchAfter.nonEmpty) {
      builder.autoarray("search_after", request.searchAfter)
    }

    if (request.sorts.nonEmpty) {
			builder.startArray("sort")
			// Workaround for bug where separator is not added with rawValues
      val arrayBody = request.sorts.map(s => SortContentBuilder(s).string).mkString(",")
      builder.rawValue(arrayBody)
			builder.endArray()
    }

    request.trackScores.map(builder.field("track_scores", _))

    request.highlight.foreach { highlight =>
      builder.rawField("highlight", HighlightFieldBuilderFn(highlight.fields))
    }

    if (request.suggs.nonEmpty) {
      builder.startObject("suggest")
      request.suggs.foreach {
        case term: TermSuggestionDefinition =>
          builder.startObject(term.name)
          term.text.foreach(builder.field("text", _))
          builder.startObject("term")
          builder.field("field", term.fieldname)
          term.analyzer.foreach(builder.field("analyzer", _))
          term.lowercaseTerms.foreach(builder.field("lowercase_terms", _))
          term.maxEdits.foreach(builder.field("max_edits", _))
          term.minWordLength.foreach(builder.field("min_word_length", _))
          term.maxInspections.foreach(builder.field("max_inspections", _))
          term.minDocFreq.foreach(builder.field("min_doc_freq", _))
          term.maxTermFreq.foreach(builder.field("max_term_freq", _))
          term.prefixLength.foreach(builder.field("prefix_length", _))
          term.size.foreach(builder.field("size", _))
          term.shardSize.foreach(builder.field("shard_size", _))
          term.sort.map(EnumConversions.sortBy).foreach(builder.field("sort", _))
          term.stringDistance.map(EnumConversions.stringDistance).foreach(builder.field("string_distance", _))
          term.suggestMode.map(EnumConversions.suggestMode).foreach(builder.field("suggest_mode", _))
          builder.endObject()
          builder.endObject()
      }
      builder.endObject()
    }

    if (request.storedFields.nonEmpty) {
      builder.array("stored_fields", request.storedFields.toArray)
    }

    if (request.indexBoosts.nonEmpty) {
      builder.startArray("indices_boost")
      request.indexBoosts.foreach { case (name, double) =>
        builder.startObject()
        builder.field(name, double)
        builder.endObject()
      }
      builder.endArray()
    }

    // source filtering
    request.fetchContext foreach { context =>
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

    // aggregations
    if (request.aggs.nonEmpty) {
      builder.startObject("aggs")
      request.aggs.foreach { agg =>
        builder.rawField(agg.name, AggregationBuilderFn(agg))
      }
      builder.endObject()
    }

    builder.endObject()
    builder
  }
}
