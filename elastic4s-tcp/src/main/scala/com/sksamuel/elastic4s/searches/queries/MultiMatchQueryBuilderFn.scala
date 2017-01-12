package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.index.query.{MultiMatchQueryBuilder, Operator, QueryBuilders}

object MultiMatchQueryBuilderFn {
  def apply(q: MultiMatchQueryDefinition): MultiMatchQueryBuilder = {
    val _builder = QueryBuilders.multiMatchQuery(q.text)
    q.minimumShouldMatch.foreach(_builder.minimumShouldMatch)
    q.fuzzyRewrite.foreach(_builder.fuzzyRewrite)
    q.analyzer.foreach(_builder.analyzer)
    q.fields.foreach {
      case (name, -1F) => _builder.field(name)
      case (name, boost) => _builder.field(name, boost)
    }
    q.cutoffFrequency.map(_.toFloat).foreach(_builder.cutoffFrequency)
    q.fuzziness.foreach(_builder.fuzziness)
    q.maxExpansions.foreach(_builder.maxExpansions)
    q.slop.foreach(_builder.slop)
    q.lenient.foreach(_builder.lenient)
    q.queryName.foreach(_builder.queryName)
    q.boost.map(_.toFloat).foreach(_builder.boost)
    q.prefixLength.foreach(_builder.prefixLength)
    q.zeroTermsQuery.map(_.toUpperCase).map(org.elasticsearch.index.search.MatchQuery.ZeroTermsQuery.valueOf).foreach(_builder.zeroTermsQuery)
    q.tieBreaker.map(_.toFloat).foreach(_builder.tieBreaker)
    q.operator.map(Operator.fromString).foreach(_builder.operator)
    q.`type`.map(_.name).map(MultiMatchQueryBuilder.Type.valueOf).foreach(_builder.`type`)
    _builder
  }
}
