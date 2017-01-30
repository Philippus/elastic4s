package com.sksamuel.elastic4s.searches.queries.`match`

import org.elasticsearch.index.query.{MatchQueryBuilder, QueryBuilders}
import org.elasticsearch.index.search.MatchQuery

object MatchQueryBuilder {
  def apply(q: MatchQueryDefinition): MatchQueryBuilder = {
    val builder = QueryBuilders.matchQuery(q.field, q.value)
    q.analyzer.foreach(builder.analyzer)
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.cutoffFrequency.map(_.toFloat).foreach(builder.cutoffFrequency)
    q.fuzziness.foreach(builder.fuzziness)
    q.fuzzyRewrite.foreach(builder.fuzzyRewrite)
    q.fuzzyTranspositions.foreach(builder.fuzzyTranspositions)
    q.lenient.foreach(builder.lenient)
    q.maxExpansions.foreach(builder.maxExpansions)
    q.minimumShouldMatch.foreach(builder.minimumShouldMatch)
    q.operator.foreach(builder.operator)
    q.prefixLength.foreach(builder.prefixLength)
    q.queryName.foreach(builder.queryName)
    q.slop.foreach(builder.slop)
    q.zeroTerms.map(_.toUpperCase).map(MatchQuery.ZeroTermsQuery.valueOf).foreach(builder.zeroTermsQuery)
    builder
  }
}
