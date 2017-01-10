package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.QueryDefinition
import org.elasticsearch.index.query.ConstantScoreQueryBuilder

case class ConstantScoreDefinition(builder: ConstantScoreQueryBuilder) extends QueryDefinition {
  def boost(b: Double): QueryDefinition = {
    builder.boost(b.toFloat)
    this
  }
}
