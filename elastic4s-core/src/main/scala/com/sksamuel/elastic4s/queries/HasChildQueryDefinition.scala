package com.sksamuel.elastic4s.queries

import com.sksamuel.elastic4s.DefinitionAttributes.DefinitionAttributeBoost
import org.elasticsearch.index.query.QueryBuilders

case class HasChildQueryDefinition(`type`: String, q: QueryDefinition)
  extends QueryDefinition with DefinitionAttributeBoost {

  val builder = QueryBuilders.hasChildQuery(`type`, q.builder)
  val _builder = builder

  /**
   * Defines the minimum number of children that are required to match for the parent to be considered a match.
   */
  def minChildren(min: Int): HasChildQueryDefinition = {
    builder.minChildren(min)
    this
  }

  /**
   * Configures at what cut off point only to evaluate parent documents that contain the matching parent id terms
   * instead of evaluating all parent docs.
   */
  def shortCircuitCutoff(shortCircuitCutoff: Int): HasChildQueryDefinition = {
    builder.setShortCircuitCutoff(shortCircuitCutoff)
    this
  }

  /**
   * Defines the maximum number of children that are required to match for the parent to be considered a match.
   */
  def maxChildren(max: Int): HasChildQueryDefinition = {
    builder.maxChildren(max)
    this
  }

  /**
   * Defines how the scores from the matching child documents are mapped into the parent document.
   */
  def scoreMode(scoreMode: String): HasChildQueryDefinition = {
    builder.scoreMode(scoreMode)
    this
  }

  /**
   * Defines how the scores from the matching child documents are mapped into the parent document.
   */
  @deprecated("use scoreMode", "2.1.0")
  def scoreType(scoreType: String): HasChildQueryDefinition = {
    builder.scoreType(scoreType)
    this
  }

  def queryName(name: String) = {
    builder.queryName(name)
    this
  }
}
