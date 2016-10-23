package com.sksamuel.elastic4s.queries

import com.sksamuel.elastic4s.DefinitionAttributes.{DefinitionAttributeBoost, DefinitionAttributeCutoffFrequency, DefinitionAttributeFuzziness, DefinitionAttributeFuzzyRewrite, DefinitionAttributePrefixLength}
import com.sksamuel.elastic4s.analyzers.Analyzer
import org.elasticsearch.index.query.QueryBuilders

case class MatchPhraseDefinition(field: String, value: Any)
  extends QueryDefinition
    with DefinitionAttributeBoost
    with DefinitionAttributeFuzziness
    with DefinitionAttributeFuzzyRewrite
    with DefinitionAttributePrefixLength
    with DefinitionAttributeCutoffFrequency {

  val builder = QueryBuilders.matchPhraseQuery(field, value.toString)
  val _builder = builder

  def analyzer(a: Analyzer): MatchPhraseDefinition = {
    builder.analyzer(a.name)
    this
  }

  def slop(s: Int): MatchPhraseDefinition = {
    builder.slop(s)
    this
  }


  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}
