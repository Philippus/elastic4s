package com.sksamuel.elastic4s2.search.queries.funcscorer

import com.sksamuel.elastic4s2.search.QueryDefinition
import org.elasticsearch.common.lucene.search.function.{CombineFunction, FiltersFunctionScoreQuery}
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder

case class FunctionScoreQueryDefinition(query: Option[QueryDefinition] = None,
                                        functions: Seq[FilterFunctionDefinition[_]] = Nil,
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

  def boost(boost: Double): FunctionScoreQueryDefinition = copy(boost = Option(boost))
  def minScore(min: Double): FunctionScoreQueryDefinition = copy(minScore = Option(min))
  def maxBoost(boost: Double): FunctionScoreQueryDefinition = copy(boost = Option(boost))
  def scoreMode(mode: FiltersFunctionScoreQuery.ScoreMode) = copy(scoreMode = Some(mode))
  def boostMode(mode: CombineFunction): FunctionScoreQueryDefinition = copy(boostMode = Some(mode))

  def withQuery(query: QueryDefinition): FunctionScoreQueryDefinition = copy(query = Some(query))

  def withFunctions(first: FilterFunctionDefinition[_],
                    rest: FilterFunctionDefinition[_]*): FunctionScoreQueryDefinition =
    withFunctions(first +: rest)

  def withFunctions(functions: Iterable[FilterFunctionDefinition[_]]): FunctionScoreQueryDefinition =
    copy(functions = functions.toSeq)
}

case class FilterFunctionDefinition[T](score: ScoreFunctionDefinition,
                                       filter: Option[QueryDefinition] = None) {
  def builder: FilterFunctionBuilder = {
    filter.fold(new FilterFunctionBuilder(score.builder)) { q =>
      new FilterFunctionBuilder(q.builder, score.builder)
    }
  }
}