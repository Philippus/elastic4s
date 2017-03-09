package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.common.unit.Fuzziness
import org.elasticsearch.index.query.{FuzzyQueryBuilder, QueryBuilders}

object FuzzyQueryBuilderFn {
  def apply(q: FuzzyQueryDefinition): FuzzyQueryBuilder = {
    val builder = QueryBuilders.fuzzyQuery(q.field, q.termValue.toString)
    q.maxExpansions.foreach(builder.maxExpansions)
    q.fuzziness.map(Fuzziness.build).foreach(builder.fuzziness)
    q.prefixLength.foreach(builder.prefixLength)
    q.transpositions.foreach(builder.transpositions)
    q.queryName.foreach(builder.queryName)
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.rewrite.foreach(builder.rewrite)
    builder
  }
}
