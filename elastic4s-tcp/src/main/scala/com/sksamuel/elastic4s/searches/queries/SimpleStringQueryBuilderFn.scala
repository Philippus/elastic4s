package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.index.query.{Operator, QueryBuilders, SimpleQueryStringBuilder}

object SimpleStringQueryBuilderFn {
  def apply(q: SimpleStringQueryDefinition): SimpleQueryStringBuilder = {
    val builder = QueryBuilders.simpleQueryStringQuery(q.query)
    q.queryName.foreach(builder.queryName)
    q.analyzer.foreach(builder.analyzer)
    q.analyzeWildcard.foreach(builder.analyzeWildcard)
    if (q.flags.nonEmpty)
      builder.flags(
        q.flags.map(_.name)
          .map(org.elasticsearch.index.query.SimpleQueryStringFlag.valueOf): _*)

    q.fields.foreach {
      case (name, -1D) => builder.field(name)
      case (name, boost) => builder.field(name, boost.toFloat)
    }
    q.lenient.foreach(builder.lenient)
    q.minimumShouldMatch.map(_.toString).foreach(builder.minimumShouldMatch)
    q.operator.map(Operator.fromString).foreach(builder.defaultOperator)
    builder
  }
}
