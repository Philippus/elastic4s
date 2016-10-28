package com.sksamuel.elastic4s2.search.queries.funcscorer

abstract class DecayScoreDefinition[T] extends ScoreDefinition[T] {
  val builder: DecayFunctionBuilder[T]
}
