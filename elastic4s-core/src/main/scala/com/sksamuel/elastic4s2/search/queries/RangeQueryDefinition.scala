package com.sksamuel.elastic4s2.search.queries

import com.sksamuel.elastic4s2.DefinitionAttributes.DefinitionAttributeBoost
import org.elasticsearch.index.query.QueryBuilders

case class RangeQueryDefinition(field: String) extends MultiTermQueryDefinition with DefinitionAttributeBoost {

  val builder = QueryBuilders.rangeQuery(field)
  val _builder = builder

  def from(f: Any) = {
    builder.from(f)
    this
  }

  def to(t: Any) = {
    builder.to(t)
    this
  }

  def timeZone(timeZone: String): RangeQueryDefinition = {
    builder.timeZone(timeZone)
    this
  }

  def gte(d: String): RangeQueryDefinition = {
    builder.gte(d)
    this
  }

  def gte(d: Double): RangeQueryDefinition = {
    builder.gte(d)
    this
  }

  def lte(d: String): RangeQueryDefinition = {
    builder.lte(d)
    this
  }

  def lte(d: Double): RangeQueryDefinition = {
    builder.lte(d)
    this
  }

  def includeLower(includeLower: Boolean) = {
    builder.includeLower(includeLower)
    this
  }

  def includeUpper(includeUpper: Boolean) = {
    builder.includeUpper(includeUpper)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}
