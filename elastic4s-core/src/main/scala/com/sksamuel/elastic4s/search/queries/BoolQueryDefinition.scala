package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.search.QueryDefinition
import org.elasticsearch.index.query.QueryBuilders

class BoolQueryDefinition extends QueryDefinition {

  val builder = QueryBuilders.boolQuery()

  def adjustPureNegative(adjustPureNegative: Boolean): this.type = {
    builder.adjustPureNegative(adjustPureNegative)
    this
  }

  def boost(boost: Double): this.type = {
    builder.boost(boost.toFloat)
    this
  }

  def disableCoord(disableCoord: Boolean): this.type = {
    builder.disableCoord(disableCoord: Boolean)
    this
  }

  def filter(first: QueryDefinition, rest: QueryDefinition*): this.type = filter(first +: rest)
  def filter(queries: Iterable[QueryDefinition]): this.type = {
    queries.foreach(builder filter _.builder)
    this
  }

  def minimumShouldMatch(minimumShouldMatch: String): this.type = {
    builder.minimumShouldMatch(minimumShouldMatch: String)
    this
  }

  def minimumShouldMatch(minimumNumberShouldMatch: Int): this.type = {
    builder.minimumNumberShouldMatch(minimumNumberShouldMatch: Int)
    this
  }

  def must(queries: QueryDefinition*): this.type = {
    queries.foreach(builder must _.builder)
    this
  }

  def must(queries: Iterable[QueryDefinition]): this.type = {
    queries.foreach(builder must _.builder)
    this
  }

  def not(queries: QueryDefinition*): this.type = {
    queries.foreach(builder mustNot _.builder)
    this
  }

  def not(queries: Iterable[QueryDefinition]): this.type = {
    queries.foreach(builder mustNot _.builder)
    this
  }

  def should(queries: QueryDefinition*): this.type = {
    queries.foreach(builder should _.builder)
    this
  }

  def should(queries: Iterable[QueryDefinition]): this.type = {
    queries.foreach(builder should _.builder)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}
