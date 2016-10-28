package com.sksamuel.elastic4s2.search.queries

import com.sksamuel.elastic4s2.DefinitionAttributes.{DefinitionAttributeBoost, DefinitionAttributePrefixLength}
import org.elasticsearch.common.unit.Fuzziness
import org.elasticsearch.index.query.QueryBuilders

@deprecated("Fuzzy queries are not useful enough and will be removed in a future version", "5.0.0")
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
