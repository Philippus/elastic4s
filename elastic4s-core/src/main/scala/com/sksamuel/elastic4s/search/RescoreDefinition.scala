package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.queries.QueryDefinition
import org.elasticsearch.search.rescore.{QueryRescoreMode, RescoreBuilder}

case class RescoreDefinition(query: QueryDefinition) {
  val builder = RescoreBuilder.queryRescorer(query.builder)
  var windowSize = 50

  def window(size: Int): RescoreDefinition = {
    this.windowSize = size
    this
  }

  def originalQueryWeight(weight: Double): RescoreDefinition = {
    builder.setQueryWeight(weight.toFloat)
    this
  }

  def rescoreQueryWeight(weight: Double): RescoreDefinition = {
    builder.setRescoreQueryWeight(weight.toFloat)
    this
  }

  def scoreMode(scoreMode: QueryRescoreMode): RescoreDefinition = {
    builder.setScoreMode(scoreMode)
    this
  }
}
