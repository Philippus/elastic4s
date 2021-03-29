package com.sksamuel.elastic4s.requests.searches.aggs.responses

case class Aggregations(data: Map[String, Any]) extends HasAggregations
