package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.elastic4s.searches.queries.QueryDefinition

case class FunctionScoreQueryDefinition(query: Option[QueryDefinition] = None,
                                        functions: Seq[ScoreFunctionDefinition] = Nil,
                                        boost: Option[Double] = None,
                                        maxBoost: Option[Double] = None,
                                        minScore: Option[Double] = None,
                                        scoreMode: Option[FunctionScoreQueryScoreMode] = None,
                                        boostMode: Option[CombineFunction] = None) extends QueryDefinition {

  def boost(boost: Double): FunctionScoreQueryDefinition = copy(boost = Option(boost))
  def minScore(min: Double): FunctionScoreQueryDefinition = copy(minScore = Option(min))
  def maxBoost(maxBoost: Double): FunctionScoreQueryDefinition = copy(maxBoost = Option(maxBoost))

  def scoreMode(mode: String): FunctionScoreQueryDefinition = scoreMode(FunctionScoreQueryScoreMode.valueOf(mode.toUpperCase))
  def scoreMode(mode: FunctionScoreQueryScoreMode): FunctionScoreQueryDefinition = copy(scoreMode = Some(mode))

  def boostMode(mode: String): FunctionScoreQueryDefinition = boostMode(CombineFunction.valueOf(mode.toUpperCase))
  def boostMode(mode: CombineFunction): FunctionScoreQueryDefinition = copy(boostMode = Some(mode))

  def query(query: QueryDefinition): FunctionScoreQueryDefinition = copy(query = Some(query))

  def functions(first: ScoreFunctionDefinition,
                rest: ScoreFunctionDefinition*): FunctionScoreQueryDefinition = functions(first +: rest)

  def functions(functions: Iterable[ScoreFunctionDefinition]): FunctionScoreQueryDefinition = copy(functions = functions.toSeq)

  @deprecated("Use 'functions' instead of this","aaeb522c04733199f4798be9fa26c6c2b1e34d0a")
  def scorers(first: ScoreFunctionDefinition, rest: ScoreFunctionDefinition*): FunctionScoreQueryDefinition = functions(first +: rest)
  @deprecated("Use 'functions' instead of this","aaeb522c04733199f4798be9fa26c6c2b1e34d0a")
  def scorers(scorers: Iterable[ScoreFunctionDefinition]): FunctionScoreQueryDefinition = functions(scorers)
  @deprecated("Use 'functions' instead of this","aaeb522c04733199f4798be9fa26c6c2b1e34d0a")
  def scoreFuncs(first: ScoreFunctionDefinition, rest: ScoreFunctionDefinition*): FunctionScoreQueryDefinition = functions(first +: rest)
  @deprecated("Use 'functions' instead of this","aaeb522c04733199f4798be9fa26c6c2b1e34d0a")
  def scoreFuncs(functions: Iterable[ScoreFunctionDefinition]): FunctionScoreQueryDefinition = copy(functions = functions.toSeq)

}
