package com.sksamuel.elastic4s.searches

import org.elasticsearch.search.rescore.{QueryRescorerBuilder, RescoreBuilder}

object RescoreBuilderFn {
  def apply(r: RescoreDefinition): QueryRescorerBuilder = {
    val builder = RescoreBuilder.queryRescorer(QueryBuilderFn(r.query))
    r.windowSize.foreach(builder.windowSize)
    r.originalQueryWeight.map(_.toFloat).foreach(builder.setQueryWeight)
    r.restoreQueryWeight.map(_.toFloat).foreach(builder.setRescoreQueryWeight)
    r.scoreMode.foreach(builder.setScoreMode)
    builder
  }
}
