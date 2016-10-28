package com.sksamuel.elastic4s2.search.queries.funcscorer

class ExponentialDecayScoreDefinition(field: String, origin: String, scale: String, offset: String)
  extends DecayScoreDefinition[ExponentialDecayScoreDefinition] {
    val builder = new ExponentialDecayFunctionBuilder(field, origin, scale, offset)
  }
