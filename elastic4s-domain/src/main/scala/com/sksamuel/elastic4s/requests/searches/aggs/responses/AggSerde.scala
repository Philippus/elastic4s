package com.sksamuel.elastic4s.requests.searches.aggs.responses

/**
  * Converts a result map into an [[AggResult]] of type T.
  */
trait AggSerde[T] {
  def read(name: String, data: Map[String, Any]): T
}
