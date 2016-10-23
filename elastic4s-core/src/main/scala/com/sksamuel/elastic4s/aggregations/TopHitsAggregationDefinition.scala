package com.sksamuel.elastic4s.aggregations

import com.sksamuel.elastic4s.ScriptDefinition
import com.sksamuel.elastic4s.sort.SortDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.sort.SortBuilder

case class TopHitsAggregationDefinition(name: String) extends AbstractAggregationDefinition {
  val builder = AggregationBuilders.topHits(name)

  def from(from: Int): this.type = {
    builder.setFrom(from)
    this
  }

  def size(size: Int): this.type = {
    builder.setSize(size)
    this
  }

  def sort(sorts: SortDefinition*): this.type = sort2(sorts.map(_.builder): _*)
  def sort2(sorts: SortBuilder*): this.type = {
    sorts.foreach(builder.addSort)
    this
  }

  def fetchSource(includes: Array[String], excludes: Array[String]): this.type = {
    builder.setFetchSource(includes, excludes)
    this
  }

  def script(name:String,script:ScriptDefinition):this.type ={
    builder.addScriptField(name,script.toJavaAPI)
    this
  }
}
