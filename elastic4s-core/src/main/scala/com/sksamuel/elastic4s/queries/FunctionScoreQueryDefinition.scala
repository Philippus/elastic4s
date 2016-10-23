package com.sksamuel.elastic4s.queries

import com.sksamuel.elastic4s.DefinitionAttributes.{DefinitionAttributeBoost, DefinitionAttributeBoostMode, DefinitionAttributeMaxBoost, DefinitionAttributeMinScore, DefinitionAttributeScoreMode}
import com.sksamuel.elastic4s.ScoreDefinition
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder

case class FunctionScoreQueryDefinition(query: QueryDefinition)
  extends QueryDefinition
    with DefinitionAttributeBoost
    with DefinitionAttributeBoostMode
    with DefinitionAttributeMaxBoost
    with DefinitionAttributeScoreMode
    with DefinitionAttributeMinScore {

  val builder = new FunctionScoreQueryBuilder(query.builder)
  val _builder = builder

  def scorers(scorers: ScoreDefinition[_]*): FunctionScoreQueryDefinition = {
    scorers.foreach(scorer => scorer._filter match {
      case None => builder.add(scorer.builder)
      case Some(filter) => builder.add(filter.builder, scorer.builder)
    })
    this
  }
}
