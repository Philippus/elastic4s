package com.sksamuel.elastic4s.query

import com.sksamuel.elastic4s.QueryDefinition
import org.elasticsearch.common.lucene.search.function.{CombineFunction, FiltersFunctionScoreQuery}
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder
import org.elasticsearch.index.query.functionscore.{FunctionScoreQueryBuilder, ScoreFunctionBuilder}

trait FilterFunctionDefinition {
  def builder: FilterFunctionBuilder
}

trait ScoreFunctionDefinition[T] {
  def builder: ScoreFunctionBuilder[T]
}

case class QueryFilterFunctionDefinition(filter: QueryDefinition, score: ScoreFunctionDefinition[_]) extends FilterFunctionDefinition {
  override def builder: FilterFunctionBuilder = {
    new FilterFunctionBuilder(filter.builder, score.builder)
  }
}

case class ScoreFilterFunctionDefinition(score: ScoreFunctionDefinition[_]) extends FilterFunctionDefinition {
  override def builder: FilterFunctionBuilder = new FilterFunctionBuilder(score.builder)
}

case class FunctionScoreQueryDefinition(query: Option[QueryDefinition] = None,
                                        functions: Seq[FilterFunctionDefinition] = Nil,
                                        boost: Option[Double] = None,
                                        maxBoost: Option[Double] = None,
                                        minScore: Option[Double] = None,
                                        scoreMode: Option[FiltersFunctionScoreQuery.ScoreMode] = None,
                                        boostMode: Option[CombineFunction] = None)
  extends QueryDefinition {

  def builder: FunctionScoreQueryBuilder = {

    val builder = query match {
      case Some(q) => new FunctionScoreQueryBuilder(q.builder, functions.map(_.builder).toArray)
      case _ => new FunctionScoreQueryBuilder(functions.map(_.builder).toArray)
    }

    boost.map(_.toFloat).foreach(builder.boost)
    maxBoost.map(_.toFloat).foreach(builder.maxBoost)
    minScore.map(_.toFloat).foreach(builder.setMinScore)
    boostMode.foreach(builder.boostMode)
    scoreMode.foreach(builder.scoreMode)
    builder
  }

  def boost(boost: Double) = copy(boost = Option(boost))
  def minScore(min: Double) = copy(minScore = Option(min))
  def maxBoost(boost: Double) = copy(boost = Option(boost))
  def scoreMode(mode: FiltersFunctionScoreQuery.ScoreMode) = copy(scoreMode = Some(mode))
  def boostMode(mode: CombineFunction) = copy(boostMode = Some(mode))

  def withQuery(query: QueryDefinition) = copy(query = Some(query))

  def withFunctions(first: FilterFunctionDefinition, rest: FilterFunctionDefinition*): FunctionScoreQueryDefinition =
    withFunctions(first +: rest)

  def withFunctions(functions: Iterable[FilterFunctionDefinition]): FunctionScoreQueryDefinition =
    copy(functions = functions.toSeq)
}
