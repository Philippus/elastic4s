package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.sort.SortDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.tophits.TopHitsAggregationBuilder

case class TopHitsAggregationDefinition(name: String) extends AggregationDefinition {

  type B = TopHitsAggregationBuilder
  val builder: B = AggregationBuilders.topHits(name)

  def explain(explain: Boolean): this.type = {
    builder.explain(explain)
    this
  }

  def fetchSource(includes: Array[String], excludes: Array[String]): this.type = {
    builder.fetchSource(includes, excludes)
    this
  }

  def fetchSource(fetchSource: Boolean): this.type = {
    builder.fetchSource(fetchSource)
    this
  }

  def size(size: Int): this.type = {
    builder.size(size)
    this
  }

  @deprecated("use sortBy", "5.0.0")
  def sort(first: SortDefinition[_], rest: SortDefinition[_]*): this.type = sortBy(first +: rest)

  @deprecated("use sortBy", "5.0.0")
  def sort(sorts: Iterable[SortDefinition[_]]): this.type = {
    sorts.map(_.builder).foreach(builder.sort)
    this
  }

  def sortBy(first: SortDefinition[_], rest: SortDefinition[_]*): this.type = sortBy(first +: rest)
  def sortBy(sorts: Iterable[SortDefinition[_]]): this.type = {
    sorts.map(_.builder).foreach(builder.sort)
    this
  }

  def storedField(storedField: String): this.type = {
    builder.storedField(storedField)
    this
  }

  def version(version: Boolean): this.type = {
    builder.version(version)
    this
  }

  def trackScores(trackScores: Boolean): this.type = {
    builder.trackScores(trackScores)
    this
  }

  def script(name: String, script: ScriptDefinition): this.type = {
    builder.scriptField(name, ScriptBuilder(script))
    this
  }
}
