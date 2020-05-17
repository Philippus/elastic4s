package com.sksamuel.elastic4s.requests.searches.aggs.responses

/**
  * Marker trait used as an upper bound for retrieving results from [[HasAggregations]].
  */
trait AggResult

/**
  * Converts a result map into an [[AggResult]] of type T.
  */
trait AggSerde[T] {
  def read(name: String, data: Map[String, Any]): T
}
