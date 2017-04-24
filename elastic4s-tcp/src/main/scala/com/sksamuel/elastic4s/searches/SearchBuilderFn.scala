package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.script.{ScriptFieldDefinition, SortBuilderFn}
import com.sksamuel.elastic4s.searches.aggs.AggregationBuilderFn
import com.sksamuel.elastic4s.searches.highlighting.HighlightBuilderFn
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.script.{Script, ScriptType}
import org.elasticsearch.search.suggest.SuggestBuilder

import scala.collection.JavaConverters._

object SearchBuilderFn {

  def apply(client: Client, search: SearchDefinition): SearchRequestBuilder = {

    val builder = client.prepareSearch(search.indexesTypes.indexes: _*)
      .setTypes(search.indexesTypes.types: _*)

    search.explain.foreach(builder.setExplain)
    search.from.foreach(builder.setFrom)
    search.indexBoosts.foreach { case (index, boost) => builder.addIndexBoost(index, boost.toFloat) }
    search.indicesOptions.foreach(builder.setIndicesOptions)
    search.minScore.map(_.toFloat).foreach(builder.setMinScore)
    search.query.map(QueryBuilderFn.apply).foreach(builder.setQuery)
    search.pref.foreach(builder.setPreference)
    search.postFilter.map(QueryBuilderFn.apply).foreach(builder.setPostFilter)
    search.requestCache.map(java.lang.Boolean.valueOf).foreach(builder.setRequestCache)
    search.routing.foreach(builder.setRouting)
    search.size.foreach(builder.setSize)
    search.searchType.foreach(builder.setSearchType)
    search.trackScores.foreach(builder.setTrackScores)
    search.terminateAfter.foreach(builder.setTerminateAfter)
    search.timeout.map(dur => TimeValue.timeValueNanos(dur.toNanos)).foreach(builder.setTimeout)
    search.keepAlive.foreach(builder.setScroll)
    search.version.foreach(builder.setVersion)

    search.fetchContext.foreach { context =>
      builder.setFetchSource(context.fetchSource)
      if (context.fetchSource) {
        builder.setFetchSource(context.includes(), context.excludes())
      }
    }

    if (search.storedFields.nonEmpty)
      builder.storedFields(search.storedFields: _*)

    if (search.aggs.nonEmpty) {
      search.aggs.map(AggregationBuilderFn.apply).foreach {
        case Left(agg) => builder.addAggregation(agg)
        case Right(pipe) => builder.addAggregation(pipe)
      }
    }

    if (search.inners.nonEmpty) {
      for (inner <- search.inners)
        sys.error("todo")
      // builder.(inner.name, inner.inner)
    }

    if (search.sorts.nonEmpty)
      search.sorts.foreach { sort =>
        builder.addSort(SortBuilderFn.apply(sort))
      }

    if (search.scriptFields.nonEmpty)
      search.scriptFields.foreach {
        case ScriptFieldDefinition(name, script, None, None, _, ScriptType.INLINE) =>
          builder.addScriptField(name, new Script(script))
        case ScriptFieldDefinition(name, script, lang, params, options, scriptType) =>
          builder.addScriptField(name, new Script(scriptType, lang.getOrElse(Script.DEFAULT_SCRIPT_LANG), script,
            options.map(_.asJava).getOrElse(new java.util.HashMap()),
            params.map(_.asJava).getOrElse(new java.util.HashMap())))
      }

    if (search.suggs.nonEmpty) {
      val suggest = new SuggestBuilder()
      search.suggs.foreach { sugg => suggest.addSuggestion(sugg.name, sugg.builder) }
      builder.suggest(suggest)
    }

    search.highlight.foreach { highlight =>
      val highlightBuilder = HighlightBuilderFn(highlight.options, highlight.fields.toSeq)
      builder.highlighter(highlightBuilder)
    }

    search.rescorers.map(RescoreBuilderFn.apply).foreach(builder.addRescorer)

    if (search.stats.nonEmpty)
      builder.setStats(search.stats: _*)

    builder
  }
}
