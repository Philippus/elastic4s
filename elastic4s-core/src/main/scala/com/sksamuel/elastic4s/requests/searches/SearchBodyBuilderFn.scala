package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.EnumConversions
import com.sksamuel.elastic4s.handlers.common.FetchSourceContextBuilderFn
import com.sksamuel.elastic4s.handlers.script.ScriptBuilderFn
import com.sksamuel.elastic4s.handlers.searches.HighlightBuilderFn
import com.sksamuel.elastic4s.handlers.searches.collapse.CollapseBuilderFn
import com.sksamuel.elastic4s.handlers.searches.knn.KnnBuilderFn
import com.sksamuel.elastic4s.handlers.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.handlers.searches.queries.sort.SortBuilderFn
import com.sksamuel.elastic4s.handlers.searches.suggestion.{CompletionSuggestionBuilderFn, PhraseSuggestion, PhraseSuggestionBuilderFn, TermSuggestionBuilderFn}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{AbstractAggregation, AggregationBuilderFn}
import com.sksamuel.elastic4s.requests.searches.suggestion.{CompletionSuggestion, TermSuggestion}

object SearchBodyBuilderFn {

  def apply(request: SearchRequest, customAggregation: PartialFunction[AbstractAggregation, XContentBuilder] = defaultCustomAggregationHandler): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    request.timeout.map(_.toMillis.toString + "ms").foreach(builder.field("timeout", _))
    request.terminateAfter.map(_.toString).foreach(builder.field("terminate_after", _))
    request.version.map(_.toString).foreach(builder.field("version", _))
    request.seqNoPrimaryTerm.map(_.toString).foreach(builder.field("seq_no_primary_term", _))

    request.query.map(QueryBuilderFn.apply).foreach(x => builder.rawField("query", x.string))
    request.postFilter.map(QueryBuilderFn.apply).foreach(x => builder.rawField("post_filter", x.string))
    request.collapse.map(CollapseBuilderFn.apply).foreach(x => builder.rawField("collapse", x.string))
    request.knn.map(KnnBuilderFn.apply).foreach(x => builder.rawField("knn", x.string))

    if (request.multipleKnn.nonEmpty) {
      builder.startArray("knn")
      val arrayBody: String = request.multipleKnn.map(KnnBuilderFn.apply).map(_.string).mkString(",")
      builder.rawValue(arrayBody)
      builder.endArray()
    }

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

    request.pit.foreach{pit  =>
      builder.startObject("pit")
      builder.field("id", pit.id)
      pit.keepAlive.foreach{keepAlive =>
        builder.field("keep_alive", s"${keepAlive.toSeconds}s")
      }
      builder.endObject()
    }

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
        builder.rawField(agg.name, AggregationBuilderFn(agg, customAggregation))
      }
      builder.endObject()
    }

    if (request.runtimeMappings.nonEmpty) {
      builder.startObject("runtime_mappings")
      request.runtimeMappings.foreach { mapping =>
        builder.startObject(mapping.field)
        builder.field("type", mapping.`type`)

        // format is only allowed with a type of date
        mapping.format.foreach(builder.field("format", _))
        mapping.script.foreach {
          script => builder.rawField("script", ScriptBuilderFn(script))
        }
        if (mapping.fields.nonEmpty) {
          builder.startObject("fields")
          mapping.fields.foreach {
            field =>
              builder.startObject(field.name)
              builder.field("type", field.`type`)
              builder.endObject()
          }
          builder.endObject()
        }
        builder.endObject()
      }
      builder.endObject()
    }

    request.trackHits.map(builder.autofield("track_total_hits", _))

    if (request.ext.nonEmpty) {
      builder.autofield("ext", request.ext)
    }

    builder.endObject()
  }
}
