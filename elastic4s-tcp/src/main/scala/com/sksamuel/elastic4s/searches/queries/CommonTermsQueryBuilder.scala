package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.index.query.{CommonTermsQueryBuilder, Operator, QueryBuilders}

object CommonTermsQueryBuilder {
  def apply(q: CommonTermsQueryDefinition): CommonTermsQueryBuilder = {
    val _builder = QueryBuilders.commonTermsQuery(q.name, q.text)
    q.analyzer.foreach(_builder.analyzer)
    q.cutoffFrequency.map(_.toFloat).foreach(_builder.cutoffFrequency)
    q.highFreqMinimumShouldMatch.map(_.toString).foreach(_builder.highFreqMinimumShouldMatch)
    q.lowFreqMinimumShouldMatch.map(_.toString).foreach(_builder.lowFreqMinimumShouldMatch)
    q.queryName.foreach(_builder.queryName)
    q.boost.map(_.toFloat).foreach(_builder.boost)
    q.lowFreqOperator.map(Operator.fromString).foreach(_builder.lowFreqOperator)
    q.highFreqOperator.map(Operator.fromString).foreach(_builder.highFreqOperator)
    _builder
  }
}
