package com.sksamuel.elastic4s2.search.queries.funcscorer

import org.elasticsearch.index.query.functionscore.LinearDecayFunctionBuilder

case class LinearDecayScoreDefinition(field: String, origin: String, scale: String, offset: String)
  extends DecayScoreDefinition[LinearDecayScoreDefinition] {
    val builder = new LinearDecayFunctionBuilder(field, origin, scale, offset)
  }
