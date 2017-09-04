package com.sksamuel.elastic4s.http.search

import com.sksamuel.elastic4s.http.SourceAsContentBuilder
import com.sksamuel.elastic4s.http.search.aggs.AggregationBuilderFn
import com.sksamuel.elastic4s.http.search.collapse.CollapseBuilderFn
import com.sksamuel.elastic4s.http.search.queries.{QueryBuilderFn, SortContentBuilder}
import com.sksamuel.elastic4s.searches.SearchDefinition
import com.sksamuel.elastic4s.searches.suggestion.{CompletionSuggestionDefinition, PhraseSuggestionDefinition, TermSuggestionDefinition}
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

import scala.collection.JavaConverters._

object SearchBodyBuilderFn {

  def apply(request: SearchDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject()

    request.query.map(QueryBuilderFn.apply).foreach(x => builder.rawField("query", new BytesArray(x.string), XContentType.JSON))
    request.postFilter.map(QueryBuilderFn.apply).foreach(x => builder.rawField("post_filter", new BytesArray(x.string), XContentType.JSON))
    request.collapse.map(CollapseBuilderFn.apply).foreach(x => builder.rawField("collapse", new BytesArray(x.string), XContentType.JSON))

    request.from.foreach(builder.field("from", _))
    request.size.foreach(builder.field("size", _))

    if (request.explain.contains(true)) {
      builder.field("explain", true)
    }

    request.minScore.foreach(builder.field("min_score", _))
    if (request.searchAfter.nonEmpty) {
      builder.field("search_after", request.searchAfter.asJava)
    }

    if (request.sorts.nonEmpty) {
			builder.startArray("sort")
			// Workaround for bug where separator is not added with rawValues
			val arrayBody = new BytesArray(request.sorts.map(s => SortContentBuilder(s).string).mkString(","))
			builder.rawValue(arrayBody, XContentType.JSON)
			builder.endArray()
    }

    request.trackScores.map(builder.field("track_scores", _))

    request.highlight.foreach { highlight =>
      builder.rawField("highlight", HighlightFieldBuilderFn(highlight.fields).bytes(), XContentType.JSON)
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
          term.sort.map(_.name().toLowerCase).foreach(builder.field("sort", _))
          term.stringDistance.map(_.name.toLowerCase).foreach(builder.field("string_distance", _))
          term.suggestMode.map(_.name().toLowerCase).foreach(builder.field("suggest_mode", _))
          builder.endObject()
          builder.endObject()
        case completion: CompletionSuggestionDefinition =>
          builder.startObject(completion.name)

          completion.text.foreach(builder.field("text", _))
          completion.prefix.foreach(builder.field("prefix", _))

          completion.regex.foreach(builder.field("value", _))

          builder.startObject("completion")

          builder.field("field", completion.fieldname)

          completion.analyzer.foreach(builder.field("analyzer", _))
          completion.size.foreach(builder.field("size", _))
          completion.shardSize.foreach(builder.field("shard_size", _))

          if(completion.regex.isDefined) {
            builder.startObject("regex")
            completion.maxDeterminizedStates.foreach(builder.field("max_determinized_states", _))
            builder.endObject()
          }

          builder.startObject("fuzzy")
          completion.fuzziness.map(_.toString).foreach(builder.field("fuzziness", _))
          completion.fuzzyMinLength.foreach(builder.field("min_length", _))
          completion.fuzzyPrefixLength.foreach(builder.field("prefix_length", _))
          completion.transpositions.foreach(builder.field("transpositions", _))
          completion.unicodeAware.foreach(builder.field("unicode_aware", _))
          builder.endObject()

          if(completion.contexts.nonEmpty) {
            builder.startObject("contexts")
            completion.contexts.foreach {
              case (key, value) =>
                builder.startArray(key)
                value.foreach { context =>
                  builder.startObject()
                  builder.field("context", context.name)
                  builder.field("boost", context.boost)
                  builder.field("prefix", context.prefix)
                  builder.endObject()
                }
                builder.endArray()
            }
            builder.endObject()
          }
          builder.endObject()
          builder.endObject()
        case phrase: PhraseSuggestionDefinition =>
          builder.startObject(phrase.name)
          phrase.text.foreach(builder.field("text", _))
          builder.startObject("phrase")

          builder.field("field", phrase.fieldname)
          phrase.analyzer.foreach(builder.field("analyzer", _))

          phrase.confidence.foreach(builder.field("confidence", _))
          phrase.forceUnigrams.foreach(builder.field("force_unigrams", _))
          phrase.gramSize.foreach(builder.field("gram_size", _))
          phrase.maxErrors.foreach(builder.field("max_error", _))
          phrase.realWordErrorLikelihood.foreach(builder.field("real_word_error_likelihood", _))
          phrase.separator.foreach(builder.field("separator", _))
          phrase.tokenLimit.foreach(builder.field("token_limit", _))
          phrase.size.foreach(builder.field("size", _))
          phrase.shardSize.foreach(builder.field("shard_size", _))

          builder.startObject("collate")
          builder.startObject("query")
          phrase.collateQuery.foreach(t => builder.rawField("inline", new BytesArray(t.getIdOrCode)))
          builder.endObject()
          phrase.collatePrune.foreach(builder.field("prune", _))
          builder.rawField("params", SourceAsContentBuilder(phrase.collateParams).bytes())
          builder.endObject()

          builder.startObject("highlight")
          phrase.preTag.foreach(builder.field("pre_tag", _))
          phrase.postTag.foreach(builder.field("post_tag", _))
          builder.endObject()

          builder.endObject()
          builder.endObject()
      }
      builder.endObject()
    }

    if (request.storedFields.nonEmpty) {
      builder.field("stored_fields", request.storedFields.asJava)
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
          builder.field("includes", context.includes.toList.asJava)
          builder.field("excludes", context.excludes.toList.asJava)
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
        builder.rawField(agg.name, AggregationBuilderFn(agg).bytes, XContentType.JSON)
      }
      builder.endObject()
    }

    builder.endObject()
    builder
  }
}
