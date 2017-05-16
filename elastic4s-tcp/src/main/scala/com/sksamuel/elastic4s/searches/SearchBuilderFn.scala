package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.{EnumConversions, ScriptBuilder}
import com.sksamuel.elastic4s.script.{ScriptFieldDefinition, ScriptType, SortBuilderFn}
import com.sksamuel.elastic4s.searches.aggs.AggregationBuilderFn
import com.sksamuel.elastic4s.searches.collapse.CollapseBuilderFn
import com.sksamuel.elastic4s.searches.highlighting.HighlightBuilderFn
import com.sksamuel.elastic4s.searches.sort.SortDefinition
import com.sksamuel.elastic4s.searches.suggestions.SuggestionBuilderFn
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.script.Script
import org.elasticsearch.search.sort.SortBuilder
import org.elasticsearch.search.suggest.SuggestBuilder

object SearchBuilderFn {

  def apply(client: Client, search: SearchDefinition): SearchRequestBuilder = {

    val builder = client.prepareSearch(search.indexesTypes.indexes: _*)
      .setTypes(search.indexesTypes.types: _*)

    search.explain.foreach(builder.setExplain)
    search.from.foreach(builder.setFrom)
    search.indexBoosts.foreach { case (index, boost) => builder.addIndexBoost(index, boost.toFloat) }
    search.indicesOptions.map(EnumConversions.indicesopts).foreach(builder.setIndicesOptions)
    search.minScore.map(_.toFloat).foreach(builder.setMinScore)
    search.query.map(QueryBuilderFn.apply).foreach(builder.setQuery)
    search.pref.foreach(builder.setPreference)
    search.postFilter.map(QueryBuilderFn.apply).foreach(builder.setPostFilter)
    search.requestCache.map(java.lang.Boolean.valueOf).foreach(builder.setRequestCache)
    search.routing.foreach(builder.setRouting)
    search.size.foreach(builder.setSize)
    search.searchType.map(EnumConversions.searchType).foreach(builder.setSearchType)
    search.trackScores.foreach(builder.setTrackScores)
    search.terminateAfter.foreach(builder.setTerminateAfter)
    search.timeout.map(dur => TimeValue.timeValueNanos(dur.toNanos)).foreach(builder.setTimeout)
    search.keepAlive.foreach(builder.setScroll)
    search.version.foreach(builder.setVersion)
    search.collapse.foreach(c => builder.setCollapse(CollapseBuilderFn.apply(c)))

    search.fetchContext.foreach { context =>
      builder.setFetchSource(context.fetchSource)
      if (context.fetchSource) {
        val inc = if (context.includes.isEmpty) null else context.includes
        val exc = if (context.excludes.isEmpty) null else context.excludes
        builder.setFetchSource(inc, exc)
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

    def convertSort(sortdef: SortDefinition): SortBuilder[_] = SortBuilderFn(sortdef)
    if (search.sorts.nonEmpty)
      search.sorts.foreach { sort =>
        builder.addSort(convertSort(sort))
      }

    if (search.scriptFields.nonEmpty)
      search.scriptFields.foreach {
        case ScriptFieldDefinition(name, script, None, None, _, ScriptType.Inline) =>
          builder.addScriptField(name, new Script(script))
        case ScriptFieldDefinition(name, script, lang, params, options, scriptType) =>
          builder.addScriptField(name, ScriptBuilder(script))
      }

    if (search.suggs.nonEmpty) {
      val suggest = new SuggestBuilder()
      search.suggs.foreach { sugg => suggest.addSuggestion(sugg.name, SuggestionBuilderFn(sugg)) }
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
