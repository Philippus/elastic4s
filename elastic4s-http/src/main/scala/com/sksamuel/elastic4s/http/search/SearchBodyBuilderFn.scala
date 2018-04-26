package com.sksamuel.elastic4s.http.search

import com.sksamuel.elastic4s.http.{EnumConversions, FetchSourceContextBuilderFn, ScriptBuilderFn}
import com.sksamuel.elastic4s.http.search.aggs.AggregationBuilderFn
import com.sksamuel.elastic4s.http.search.collapse.CollapseBuilderFn
import com.sksamuel.elastic4s.http.search.queries.{QueryBuilderFn, SortBuilderFn}
import com.sksamuel.elastic4s.http.search.suggs.{
  CompletionSuggestionBuilderFn,
  PhraseSuggestionBuilderFn,
  TermSuggestionBuilderFn
}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.SearchRequest
import com.sksamuel.elastic4s.searches.suggestion.{CompletionSuggestion, PhraseSuggestion, TermSuggestion}

object SearchBodyBuilderFn {

  def apply(request: SearchRequest): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    builder.field("version", true)
    request.control.pref.foreach(builder.field("preference", _))
    request.control.timeout.map(_.toMillis + "ms").foreach(builder.field("timeout", _))
    request.control.terminateAfter.map(_.toString).foreach(builder.field("terminate_after", _))
    request.version.map(_.toString).foreach(builder.field("version", _))

    request.query.map(QueryBuilderFn.apply).foreach(x => builder.rawField("query", x.string))
    request.postFilter.map(QueryBuilderFn.apply).foreach(x => builder.rawField("post_filter", x.string))
    request.collapse.map(CollapseBuilderFn.apply).foreach(x => builder.rawField("collapse", x.string))

    request.windowing.from.foreach(builder.field("from", _))
    request.windowing.size.foreach(builder.field("size", _))

    request.profile.foreach(builder.field("profile", _))

    if (request.windowing.slice.nonEmpty) {
      builder.startObject("slice")
      builder.field("id", request.windowing.slice.get._1)
      builder.field("max", request.windowing.slice.get._2)
      builder.endObject()
    }

    if (request.meta.explain.getOrElse(false))
      builder.field("explain", true)

    request.scoring.minScore.foreach(builder.field("min_score", _))
    if (request.searchAfter.nonEmpty)
      builder.autoarray("search_after", request.searchAfter)

    if (request.fields.scriptFields.nonEmpty) {
      builder.startObject("script_fields")
      request.fields.scriptFields.foreach { field =>
        builder.startObject(field.field)
        builder.rawField("script", ScriptBuilderFn(field.script))
        builder.endObject()
      }
      builder.endObject()
    }

    if (request.scoring.rescorers.nonEmpty) {
      builder.startArray("rescore")
      request.scoring.rescorers.foreach { rescore =>
        builder.startObject()
        rescore.windowSize.foreach(builder.field("window_size", _))
        builder.startObject("query")
        builder.rawField("rescore_query", QueryBuilderFn(rescore.query))
        rescore.rescoreQueryWeight.foreach(builder.field("rescore_query_weight", _))
        rescore.originalQueryWeight.foreach(builder.field("query_weight", _))
        rescore.scoreMode.map(EnumConversions.queryRescoreMode).foreach(builder.field("score_mode", _))
        builder.endObject().endObject()
      }
      builder.endArray()
    }

    if (request.sorts.nonEmpty) {
      builder.startArray("sort")
      // Workaround for bug where separator is not added with rawValues
      val arrayBody = request.sorts.map(s => SortBuilderFn(s).string).mkString(",")
      builder.rawValue(arrayBody)
      builder.endArray()
    }

    request.scoring.trackScores.map(builder.field("track_scores", _))

    request.highlight.foreach { highlight =>
      builder.rawField("highlight", HighlightBuilderFn(highlight))
    }

    if (request.suggestions.suggs.nonEmpty) {
      builder.startObject("suggest")
      request.suggestions.globalSuggestionText.foreach(builder.field("text", _))
      request.suggestions.suggs.foreach {
        case term: TermSuggestion => builder.rawField(term.name, TermSuggestionBuilderFn(term))
        case completion: CompletionSuggestion =>
          builder.rawField(completion.name, CompletionSuggestionBuilderFn(completion))
        case phrase: PhraseSuggestion => builder.rawField(phrase.name, PhraseSuggestionBuilderFn(phrase))
      }
      builder.endObject()
    }

    if (request.fields.storedFields.nonEmpty)
      builder.array("stored_fields", request.fields.storedFields.toArray)

    if (request.indexBoosts.nonEmpty) {
      builder.startArray("indices_boost")
      request.indexBoosts.foreach {
        case (name, double) =>
          builder.startObject()
          builder.field(name, double)
          builder.endObject()
      }
      builder.endArray()
    }

    // source filtering
    request.fetchContext.foreach(FetchSourceContextBuilderFn(builder, _))

    if (request.fields.docValues.nonEmpty)
      builder.array("docvalue_fields", request.fields.docValues.toArray)

    // aggregations
    if (request.aggs.nonEmpty) {
      builder.startObject("aggs")
      request.aggs.foreach { agg =>
        builder.rawField(agg.name, AggregationBuilderFn(agg))
      }
      builder.endObject()
    }

    request.trackHits.map(builder.field("track_total_hits", _))

    builder.endObject()
  }
}
