package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.aggs.pipeline.PipelineAggregationDefinition
import com.sksamuel.elastic4s.searches.sort.SortDefinition
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.tophits.TopHitsAggregationBuilder
import org.elasticsearch.search.fetch.subphase.FetchSourceContext

case class TopHitsAggregationDefinition(name: String,
                                        explain: Option[Boolean] = None,
                                        fetchSource: Option[FetchSourceContext] = None,
                                        size: Option[Int] = None,
                                        sorts: Seq[SortDefinition] = Nil,
                                        trackScores: Option[Boolean] = None,
                                        version: Option[Boolean] = None,
                                        scripts: Map[String, ScriptDefinition] = Map.empty,
                                        storedFields: Seq[String] = Nil,
                                        pipelines: Seq[PipelineAggregationDefinition] = Nil,
                                        subaggs: Seq[AggregationDefinition] = Nil,
                                        metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = TopHitsAggregationDefinition

  type B = TopHitsAggregationBuilder
  val builder: B = AggregationBuilders.topHits(name)

  def explain(explain: Boolean): TopHitsAggregationDefinition = copy(explain = explain.some)
  def fetchSource(includes: Array[String], excludes: Array[String]): TopHitsAggregationDefinition =
    copy(fetchSource = new FetchSourceContext(true, includes, excludes).some)

  def fetchSource(fetchSource: Boolean): TopHitsAggregationDefinition =
    copy(fetchSource = new FetchSourceContext(true).some)

  def size(size: Int): TopHitsAggregationDefinition = copy(size = size.some)

  def sortBy(first: SortDefinition, rest: SortDefinition*): TopHitsAggregationDefinition = sortBy(first +: rest)
  def sortBy(sorts: Iterable[SortDefinition]): TopHitsAggregationDefinition = copy(sorts = sorts.toSeq)

  def storedField(field: String): TopHitsAggregationDefinition = storedFields(field)
  def storedFields(first: String, rest: String*): TopHitsAggregationDefinition = storedFields(first +: rest)
  def storedFields(fields: Iterable[String]): TopHitsAggregationDefinition = copy(storedFields = fields.toSeq)

  def version(version: Boolean): TopHitsAggregationDefinition = copy(version = version.some)
  def trackScores(trackScores: Boolean): TopHitsAggregationDefinition = copy(trackScores = trackScores.some)

  def script(name: String, script: ScriptDefinition): T = copy(scripts = scripts + (name -> script))

  override def pipelines(pipelines: Iterable[PipelineAggregationDefinition]): T = copy(pipelines = pipelines.toSeq)
  override def subAggregations(aggs: Iterable[AggregationDefinition]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}
