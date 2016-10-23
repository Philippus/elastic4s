package com.sksamuel.elastic4s.queries

import com.sksamuel.elastic4s.DefinitionAttributes.{DefinitionAttributeBoost, DefinitionAttributePrefixLength}
import org.elasticsearch.common.unit.Fuzziness
import org.elasticsearch.index.query.QueryBuilders

case class FuzzyQueryDefinition(field: String, termValue: Any)
  extends MultiTermQueryDefinition
    with DefinitionAttributePrefixLength
    with DefinitionAttributeBoost {

  val builder = QueryBuilders.fuzzyQuery(field, termValue.toString)
  val _builder = builder

  def fuzziness(fuzziness: Fuzziness) = {
    builder.fuzziness(fuzziness)
    this
  }

  def maxExpansions(maxExpansions: Int) = {
    builder.maxExpansions(maxExpansions)
    this
  }

  def transpositions(transpositions: Boolean) = {
    builder.transpositions(transpositions)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}
