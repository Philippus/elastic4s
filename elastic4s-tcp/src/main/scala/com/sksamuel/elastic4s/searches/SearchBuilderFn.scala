package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.EnumConversions
import com.sksamuel.elastic4s.script.SortBuilderFn
import com.sksamuel.elastic4s.searches.aggs.AggregationBuilderFn
import com.sksamuel.elastic4s.searches.collapse.CollapseBuilderFn
import com.sksamuel.elastic4s.searches.highlighting.HighlightBuilderFn
import com.sksamuel.elastic4s.searches.sort.SortDefinition
import com.sksamuel.elastic4s.searches.suggestions.SuggestionBuilderFn
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.script.Script
import org.elasticsearch.search.slice.SliceBuilder
import org.elasticsearch.search.sort.SortBuilder
import org.elasticsearch.search.suggest.SuggestBuilder

object SearchBuilderFn {

  def apply(client: Client, search: SearchDefinition): SearchRequestBuilder = {

    val builder = client
      .prepareSearch(search.indexesTypes.indexes: _*)
      .setTypes(search.indexesTypes.types: _*)

    search.meta.explain.foreach(builder.setExplain)
    search.windowing.from.foreach(builder.setFrom)
    search.indexBoosts.foreach { case (index, boost) => builder.addIndexBoost(index, boost.toFloat) }
    search.indicesOptions.map(EnumConversions.indicesopts).foreach(builder.setIndicesOptions)
    search.scoring.minScore.map(_.toFloat).foreach(builder.setMinScore)
    search.query.map(QueryBuilderFn.apply).foreach(builder.setQuery)
    search.control.pref.foreach(builder.setPreference)
    search.postFilter.map(QueryBuilderFn.apply).foreach(builder.setPostFilter)
    search.requestCache.map(java.lang.Boolean.valueOf).foreach(builder.setRequestCache)
    search.trackHits.map(builder.setTrackTotalHits)
    search.control.routing.foreach(builder.setRouting)
    search.windowing.size.foreach(builder.setSize)
    search.searchType.map(EnumConversions.searchType).foreach(builder.setSearchType)
    search.scoring.trackScores.foreach(builder.setTrackScores)
    search.control.terminateAfter.foreach(builder.setTerminateAfter)
    if (search.searchAfter.nonEmpty)
      builder.searchAfter(search.searchAfter.toArray)
    search.control.timeout.map(dur => TimeValue.timeValueNanos(dur.toNanos)).foreach(builder.setTimeout)
    search.keepAlive.foreach(builder.setScroll)
    search.windowing.slice.foreach(s => builder.slice(new SliceBuilder(s._1, s._2)))
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

    if (search.fields.storedFields.nonEmpty)
      builder.storedFields(search.fields.storedFields: _*)

    if (search.aggs.nonEmpty) {
      search.aggs.map(AggregationBuilderFn.apply).foreach {
        case Left(agg)   => builder.addAggregation(agg)
        case Right(pipe) => builder.addAggregation(pipe)
      }
    }

    if (search.inners.nonEmpty) {
      for (inner <- search.inners)
        sys.error("todo")
      // builder.(inner.name, inner.inner)
    }

    def convertSort[T <: SortBuilder[T]](sortdef: SortDefinition): SortBuilder[T] = SortBuilderFn(sortdef)

    if (search.sorts.nonEmpty)
      search.sorts.foreach { sort =>
        builder.addSort(convertSort(sort))
      }

    if (search.fields.scriptFields.nonEmpty) {
      import scala.collection.JavaConverters._
      search.fields.scriptFields.foreach { scriptfield =>
        builder.addScriptField(
          scriptfield.field,
          new Script(
            EnumConversions.scriptType(scriptfield.script.scriptType),
            scriptfield.script.lang.getOrElse(Script.DEFAULT_SCRIPT_LANG): String,
            scriptfield.script.script: String,
            scriptfield.script.options.asJava: java.util.Map[String, String],
            scriptfield.script.params.map { case (key, value) => key -> (value.toString: Object) }.asJava: java.util.Map[
              String,
              Object
            ]
          )
        )
      }
    }

    if (search.suggestions.suggs.nonEmpty) {
      val suggest = new SuggestBuilder()
      search.suggestions.globalSuggestionText.foreach(suggest.setGlobalText)
      search.suggestions.suggs.foreach { sugg =>
        suggest.addSuggestion(sugg.name, SuggestionBuilderFn(sugg))
      }
      builder.suggest(suggest)
    }

    search.highlight.foreach { highlight =>
      val highlightBuilder = HighlightBuilderFn(highlight.options, highlight.fields.toSeq)
      builder.highlighter(highlightBuilder)
    }

    if (search.meta.stats.nonEmpty)
      builder.setStats(search.meta.stats: _*)

    builder
  }
}
