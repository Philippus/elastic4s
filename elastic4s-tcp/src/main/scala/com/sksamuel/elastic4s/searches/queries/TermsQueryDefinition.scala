package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.index.query.TermsQueryBuilder

case class TermsQueryDefinition(builder: TermsQueryBuilder) extends QueryDefinition {

  def boost(boost: Double): this.type = {
    builder.boost(boost.toFloat)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}
