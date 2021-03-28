package com.sksamuel.elastic4s.requests.searches.queries.matches

case class FieldWithOptionalBoost(field: String, boost: Option[Double])
