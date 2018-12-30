package com.sksamuel.elastic4s.requests.searches.queries.funcscorer

import com.sksamuel.elastic4s.requests.searches.queries.Query

case class FunctionScoreQuery(query: Option[Query] = None,
                              functions: Seq[ScoreFunction] = Nil,
                              boost: Option[Double] = None,
                              maxBoost: Option[Double] = None,
                              minScore: Option[Double] = None,
                              scoreMode: Option[FunctionScoreQueryScoreMode] = None,
                              boostMode: Option[CombineFunction] = None)
    extends Query {

  def boost(boost: Double): FunctionScoreQuery       = copy(boost = Option(boost))
  def minScore(min: Double): FunctionScoreQuery      = copy(minScore = Option(min))
  def maxBoost(maxBoost: Double): FunctionScoreQuery = copy(maxBoost = Option(maxBoost))

  def scoreMode(mode: String): FunctionScoreQuery =
    scoreMode(FunctionScoreQueryScoreMode.valueOf(mode.toUpperCase))
  def scoreMode(mode: FunctionScoreQueryScoreMode): FunctionScoreQuery = copy(scoreMode = Some(mode))

  def boostMode(mode: String): FunctionScoreQuery          = boostMode(CombineFunction.valueOf(mode.toUpperCase))
  def boostMode(mode: CombineFunction): FunctionScoreQuery = copy(boostMode = Some(mode))

  def query(query: Query): FunctionScoreQuery = copy(query = Some(query))

  def functions(first: ScoreFunction, rest: ScoreFunction*): FunctionScoreQuery =
    functions(first +: rest)

  def functions(functions: Iterable[ScoreFunction]): FunctionScoreQuery =
    copy(functions = functions.toSeq)

  @deprecated("Use 'functions' instead of this", "aaeb522c04733199f4798be9fa26c6c2b1e34d0a")
  def scorers(first: ScoreFunction, rest: ScoreFunction*): FunctionScoreQuery =
    functions(first +: rest)
  @deprecated("Use 'functions' instead of this", "aaeb522c04733199f4798be9fa26c6c2b1e34d0a")
  def scorers(scorers: Iterable[ScoreFunction]): FunctionScoreQuery = functions(scorers)
  @deprecated("Use 'functions' instead of this", "aaeb522c04733199f4798be9fa26c6c2b1e34d0a")
  def scoreFuncs(first: ScoreFunction, rest: ScoreFunction*): FunctionScoreQuery =
    functions(first +: rest)
  @deprecated("Use 'functions' instead of this", "aaeb522c04733199f4798be9fa26c6c2b1e34d0a")
  def scoreFuncs(functions: Iterable[ScoreFunction]): FunctionScoreQuery =
    copy(functions = functions.toSeq)

}
