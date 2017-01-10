package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import org.elasticsearch.common.lucene.search.function.FiltersFunctionScoreQuery.ScoreMode
import org.elasticsearch.common.lucene.search.function.{CombineFunction, FiltersFunctionScoreQuery}
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder

case class FunctionScoreQueryDefinition(query: Option[QueryDefinition] = None,
                                        scorers: Seq[FilterFunctionDefinition] = Nil,
                                        boost: Option[Double] = None,
                                        maxBoost: Option[Double] = None,
                                        minScore: Option[Double] = None,
                                        scoreMode: Option[FiltersFunctionScoreQuery.ScoreMode] = None,
                                        boostMode: Option[CombineFunction] = None)
  extends QueryDefinition {

  def builder: FunctionScoreQueryBuilder = {

    val builder = query match {
      case Some(q) => new FunctionScoreQueryBuilder(QueryBuilderFn(q), scorers.map(_.builder).toArray)
      case _ => new FunctionScoreQueryBuilder(scorers.map(_.builder).toArray)
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

  def scoreMode(mode: String): FunctionScoreQueryDefinition = scoreMode(ScoreMode.valueOf(mode.toUpperCase))
  def scoreMode(mode: ScoreMode): FunctionScoreQueryDefinition = copy(scoreMode = Some(mode))

  def boostMode(mode: String): FunctionScoreQueryDefinition = boostMode(CombineFunction.valueOf(mode.toUpperCase))
  def boostMode(mode: CombineFunction): FunctionScoreQueryDefinition = copy(boostMode = Some(mode))

  def query(query: QueryDefinition): FunctionScoreQueryDefinition = copy(query = Some(query))

  def scorers(first: ScoreFunctionDefinition,
              rest: ScoreFunctionDefinition*): FunctionScoreQueryDefinition = scorers(first +: rest)

  def scorers(scorers: Iterable[ScoreFunctionDefinition]): FunctionScoreQueryDefinition =
    scoreFuncs(scorers.map(FilterFunctionDefinition(_)))

  def scoreFuncs(first: FilterFunctionDefinition,
                 rest: FilterFunctionDefinition*): FunctionScoreQueryDefinition = scoreFuncs(first +: rest)

  def scoreFuncs(functions: Iterable[FilterFunctionDefinition]): FunctionScoreQueryDefinition = copy(scorers = functions.toSeq)
}

case class FilterFunctionDefinition(score: ScoreFunctionDefinition,
                                    filter: Option[QueryDefinition] = None) {

  def filter(query: QueryDefinition): FilterFunctionDefinition = FilterFunctionDefinition(score, Some(query))

  def builder: FilterFunctionBuilder = {
    filter.fold(new FilterFunctionBuilder(score.builder)) { q =>
      new FilterFunctionBuilder(QueryBuilderFn(q), score.builder)
    }
  }
}
