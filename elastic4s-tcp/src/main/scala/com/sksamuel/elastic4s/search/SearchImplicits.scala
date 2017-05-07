package com.sksamuel.elastic4s.search

import cats.Show
import com.sksamuel.elastic4s.Executable
import com.sksamuel.elastic4s.script.{ScriptFieldDefinition, SortBuilderFn}
import com.sksamuel.elastic4s.searches._
import com.sksamuel.elastic4s.searches.aggs.AggregationBuilderFn
import com.sksamuel.elastic4s.searches.collapse.CollapseBuilderFn
import com.sksamuel.elastic4s.searches.highlighting.HighlightBuilderFn
import com.sksamuel.elastic4s.searches.suggestions.SuggestionBuilderFn
import org.elasticsearch.action.search.{MultiSearchResponse, SearchResponse}
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.script.{Script, ScriptType}
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.search.sort.SortBuilder
import org.elasticsearch.search.suggest.SuggestBuilder

import scala.concurrent.Future

trait SearchImplicits {

  implicit object SearchDefinitionExecutable
    extends Executable[SearchDefinition, SearchResponse, RichSearchResponse] {
    override def apply(c: Client, t: SearchDefinition): Future[RichSearchResponse] = {
      val builder = SearchBuilderFn(c, t)
      injectFutureAndMap(builder.execute)(RichSearchResponse.apply)
    }
  }

  //  implicit object SearchTemplateDefinitionExecutable
  //    extends Executable[SearchTemplateDefinition, SearchTemplateResponse, RichSearchTemplateResponse] {
  //    override def apply(client: Client, t: SearchTemplateDefinition): Future[RichSearchTemplateResponse] = {
  //      val builder = SearchTemplateAction.INSTANCE.newRequestBuilder(client)
  //      t.populate(builder)
  //      injectFutureAndMap(builder.execute)(RichSearchTemplateResponse.apply)
  //    }
  //  }

  implicit object MultiSearchDefinitionExecutable
    extends Executable[MultiSearchDefinition, MultiSearchResponse, RichMultiSearchResponse] {
    override def apply(c: Client, t: MultiSearchDefinition): Future[RichMultiSearchResponse] = {
      val builder = MultiSearchBuilderFn(c, t)
      injectFutureAndMap(builder.execute)(RichMultiSearchResponse.apply)
    }
  }

  implicit object SearchDefinitionShow extends Show[SearchDefinition] {
    override def show(search: SearchDefinition): String = {

      import scala.collection.JavaConverters._

      val builder = new SearchSourceBuilder()
      search.query.map(QueryBuilderFn.apply).foreach(builder.query)
      search.minScore.map(_.toFloat).foreach(builder.minScore)
      search.version.foreach(v => builder.version(v))
      search.postFilter.map(QueryBuilderFn.apply).foreach(builder.postFilter)
      search.explain.foreach(b => builder.explain(b))
      search.from.foreach(builder.from)
      search.size.foreach(builder.size)
      search.trackScores.foreach(builder.trackScores)
      search.terminateAfter.foreach(builder.terminateAfter)
      search.timeout.map(dur => TimeValue.timeValueNanos(dur.toNanos)).foreach(builder.timeout)
      search.indexBoosts.foreach { case (index, boost) => builder.indexBoost(index, boost.toFloat) }
      search.collapse.foreach(c => builder.collapse(CollapseBuilderFn.apply(c)))

      if (search.storedFields.nonEmpty)
        builder.storedFields(search.storedFields.asJava)

      if (search.aggs.nonEmpty)
        search.aggs.map(AggregationBuilderFn.apply).map {
          case Left(agg) => builder.aggregation(agg)
          case Right(pipe) => builder.aggregation(pipe)
        }

      if (search.inners.nonEmpty) {
        for (inner <- search.inners)
          sys.error("todo")
        // builder.(inner.name, inner.inner)
      }

      if (search.sorts.nonEmpty)
        search.sorts.foreach { sort =>
          builder.getClass.getMethod("sort", classOf[SortBuilder[_]]).invoke(builder, SortBuilderFn.apply(sort))
        }

      if (search.scriptFields.nonEmpty) {
        search.scriptFields.foreach {
          case ScriptFieldDefinition(name, script, None, None, _, ScriptType.INLINE) =>
            builder.scriptField(name, new Script(script))
          case ScriptFieldDefinition(name, script, lang, params, options, scriptType) =>
            builder.scriptField(name, new Script(scriptType, lang.getOrElse(Script.DEFAULT_SCRIPT_LANG), script,
              options.map(_.asJava).getOrElse(new java.util.HashMap()),
              params.map(_.asJava).getOrElse(new java.util.HashMap())))
        }
      }

      if (search.suggs.nonEmpty) {
        val suggest = new SuggestBuilder()
        search.globalSuggestionText.foreach(suggest.setGlobalText)
        search.suggs.foreach { sugg => suggest.addSuggestion(sugg.name, SuggestionBuilderFn(sugg)) }
        builder.suggest(suggest)
      }

      search.highlight.foreach { highlight =>
        val highlightBuilder = HighlightBuilderFn(highlight.options, highlight.fields.toSeq)
        builder.highlighter(highlightBuilder)
      }

      search.rescorers.map(RescoreBuilderFn.apply).foreach(builder.addRescorer)

      if (search.stats.nonEmpty)
        builder.stats(search.stats.asJava)

      builder.toString
    }
  }

  implicit class SearchDefinitionShowOps(f: SearchDefinition) {
    def show: String = SearchDefinitionShow.show(f)
  }

  implicit object MultiSearchDefinitionShow extends Show[MultiSearchDefinition] {
    import compat.Platform.EOL
    override def show(f: MultiSearchDefinition): String = f.searches.map(_.show).mkString("[" + EOL, "," + EOL, "]")
  }

  implicit class MultiSearchDefinitionShowOps(f: MultiSearchDefinition) {
    def show: String = MultiSearchDefinitionShow.show(f)
  }
}
