package com.sksamuel.elastic4s2.search.queries.funcscorer

import org.elasticsearch.index.query.functionscore.GaussDecayFunctionBuilder

case class GaussianDecayScoreDefinition(field: String, origin: String, scale: String, offset: String)
  extends DecayScoreDefinition[GaussianDecayScoreDefinition] {
    val builder = new GaussDecayFunctionBuilder(field, origin, scale, offset)
  }
