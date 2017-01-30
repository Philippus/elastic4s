package com.sksamuel.elastic4s.http.search

import com.sksamuel.elastic4s.searches.queries.`match`.{MatchAllQueryDefinition, MatchQueryDefinition}
import com.sksamuel.elastic4s.searches.queries.{QueryDefinition, QueryStringQueryDefinition, SimpleStringQueryDefinition, TermQueryDefinition}
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object QueryBuilderFn {
  def apply(q: QueryDefinition): XContentBuilder = q match {
    case t: TermQueryDefinition => TermQueryBodyFn(t)
    case s: SimpleStringQueryDefinition => SimpleStringBodyFn(s)
    case q: QueryStringQueryDefinition => QueryStringBodyFn(q)
    case q: MatchQueryDefinition => MatchBodyFn(q)
    case q: MatchAllQueryDefinition => MatchAllBodyFn(q)
  }
}
