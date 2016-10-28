package com.sksamuel.elastic4s.search.query

import com.sksamuel.elastic4s.search.QueryDefinition
import org.elasticsearch.index.query.ConstantScoreQueryBuilder

case class ConstantScoreDefinition(builder: ConstantScoreQueryBuilder) extends QueryDefinition {
  def boost(b: Double): QueryDefinition = {
    builder.boost(b.toFloat)
    this
  }
}
