package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.index.query.{Operator, QueryBuilders, QueryStringQueryBuilder}

object QueryStringBuilderFn {

  def builder(query: QueryStringQueryDefinition): QueryStringQueryBuilder = {
    val builder = QueryBuilders.queryStringQuery(query.query)
    query.allowLeadingWildcard.map(java.lang.Boolean.valueOf).foreach(builder.allowLeadingWildcard)
    query.analyzeWildcard.map(java.lang.Boolean.valueOf).foreach(builder.analyzeWildcard)
    query.analyzer.foreach(builder.analyzer)
    query.autoGeneratePhraseQueries.foreach(builder.autoGeneratePhraseQueries)
    query.boost.map(_.toFloat).foreach(builder.boost)
    query.defaultOperator.map(Operator.fromString).foreach(builder.defaultOperator)
    query.defaultField.foreach(builder.defaultField)
    query.enablePositionIncrements.foreach(builder.enablePositionIncrements)
    query.fields.foreach {
      case (name, -1)    => builder.field(name)
      case (name, boost) => builder.field(name, boost.toFloat)
    }
    query.fuzzyMaxExpansions.foreach(builder.fuzzyMaxExpansions)
    query.fuzzyPrefixLength.foreach(builder.fuzzyPrefixLength)
    query.fuzzyRewrite.foreach(builder.fuzzyRewrite)
    query.lenient.map(java.lang.Boolean.valueOf).foreach(builder.lenient)
    query.minimumShouldMatch.map(_.toString).foreach(builder.minimumShouldMatch)
    query.phraseSlop.foreach(builder.phraseSlop)
    query.quoteFieldSuffix.foreach(builder.quoteFieldSuffix)
    query.queryName.foreach(builder.queryName)
    query.rewrite.foreach(builder.rewrite)
    query.splitOnWhitespace.foreach(builder.splitOnWhitespace)
    query.tieBreaker.map(_.toFloat).foreach(builder.tieBreaker)
    builder
  }
}
