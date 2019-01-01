package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.{EnumConversions, XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.common.FetchSourceContextBuilderFn
import com.sksamuel.elastic4s.requests.script.ScriptBuilderFn
import com.sksamuel.elastic4s.requests.searches.aggs.AggregationBuilderFn
import com.sksamuel.elastic4s.requests.searches.collapse.CollapseBuilderFn
import com.sksamuel.elastic4s.requests.searches.queries.{QueryBuilderFn, SortBuilderFn}
import com.sksamuel.elastic4s.requests.searches.suggestion.{CompletionSuggestion, CompletionSuggestionBuilderFn, PhraseSuggestion, PhraseSuggestionBuilderFn, TermSuggestion, TermSuggestionBuilderFn}

object SearchBodyBuilderFn {

  def apply(request: SearchRequest): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    request.timeout.map(_.toMillis + "ms").foreach(builder.field("timeout", _))
    request.terminateAfter.map(_.toString).foreach(builder.field("terminate_after", _))
    request.version.map(_.toString).foreach(builder.field("version", _))

    request.query.map(QueryBuilderFn.apply).foreach(x => builder.rawField("query", x.string))
    request.postFilter.map(QueryBuilderFn.apply).foreach(x => builder.rawField("post_filter", x.string))
    request.collapse.map(CollapseBuilderFn.apply).foreach(x => builder.rawField("collapse", x.string))

    request.from.foreach(builder.field("from", _))
    request.size.foreach(builder.field("size", _))

    request.profile.foreach(builder.field("profile", _))

    request.slice.foreach { case (id, max) =>
      builder.startObject("slice")
      builder.field("id", id)
      builder.field("max", max)
      builder.endObject()
    }

    if (request.explain.getOrElse(false))
      builder.field("explain", true)

    request.minScore.foreach(builder.field("min_score", _))
    if (request.searchAfter.nonEmpty)
      builder.autoarray("search_after", request.searchAfter)

    if (request.scriptFields.nonEmpty) {
      builder.startObject("script_fields")
      request.scriptFields.foreach { field =>
        builder.startObject(field.field)
        builder.rawField("script", ScriptBuilderFn(field.script))
        builder.endObject()
      }
      builder.endObject()
    }

    if (request.rescorers.nonEmpty) {
      builder.startArray("rescore")
      request.rescorers.foreach { rescore =>
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

    request.trackScores.map(builder.field("track_scores", _))

    request.highlight.foreach { highlight =>
      builder.rawField("highlight", HighlightBuilderFn(highlight))
    }

    if (request.suggs.nonEmpty) {
      builder.startObject("suggest")
      request.globalSuggestionText.foreach(builder.field("text", _))
      request.suggs.foreach {
        case term: TermSuggestion => builder.rawField(term.name, TermSuggestionBuilderFn(term))
        case completion: CompletionSuggestion =>
          builder.rawField(completion.name, CompletionSuggestionBuilderFn(completion))
        case phrase: PhraseSuggestion => builder.rawField(phrase.name, PhraseSuggestionBuilderFn(phrase))
      }
      builder.endObject()
    }

    if (request.storedFields.nonEmpty)
      builder.array("stored_fields", request.storedFields.toArray)

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

    if (request.docValues.nonEmpty)
      builder.array("docvalue_fields", request.docValues.toArray)

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
