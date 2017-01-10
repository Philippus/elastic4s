package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.index.query.QueryBuilders

class BoolQueryDefinition extends QueryDefinition {

  val builder = QueryBuilders.boolQuery()

  def adjustPureNegative(adjustPureNegative: Boolean): BoolQueryDefinition = {
    builder.adjustPureNegative(adjustPureNegative)
    this
  }

  def boost(boost: Double): BoolQueryDefinition = {
    builder.boost(boost.toFloat)
    this
  }

  def disableCoord(disableCoord: Boolean): BoolQueryDefinition = {
    builder.disableCoord(disableCoord: Boolean)
    this
  }

  def filter(first: QueryDefinition, rest: QueryDefinition*): BoolQueryDefinition = filter(first +: rest)

  def filter(queries: Iterable[QueryDefinition]): BoolQueryDefinition = {
    queries.foreach(q => builder.filter(QueryBuilderFn(q)))
    this
  }

  def minimumShouldMatch(minimumShouldMatch: String): BoolQueryDefinition = {
    builder.minimumShouldMatch(minimumShouldMatch: String)
    this
  }

  def minimumShouldMatch(minimumNumberShouldMatch: Int): BoolQueryDefinition = {
    builder.minimumNumberShouldMatch(minimumNumberShouldMatch: Int)
    this
  }

  def must(queries: QueryDefinition*): BoolQueryDefinition = must(queries)
  def must(queries: Iterable[QueryDefinition]): BoolQueryDefinition = {
    queries.foreach(q => builder.must(QueryBuilderFn(q)))
    this
  }

  def not(queries: QueryDefinition*): BoolQueryDefinition = not(queries)
  def not(queries: Iterable[QueryDefinition]): BoolQueryDefinition = {
    queries.foreach(q => builder.mustNot(QueryBuilderFn(q)))
    this
  }

  def should(queries: QueryDefinition*): BoolQueryDefinition = should(queries)
  def should(queries: Iterable[QueryDefinition]): BoolQueryDefinition = {
    queries.foreach(q => builder.should(QueryBuilderFn(q)))
    this
  }

  def queryName(queryName: String): BoolQueryDefinition = {
    builder.queryName(queryName)
    this
  }
}
