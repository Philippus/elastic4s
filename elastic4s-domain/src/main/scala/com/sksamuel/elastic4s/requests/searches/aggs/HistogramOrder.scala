package com.sksamuel.elastic4s.requests.searches.aggs

case class HistogramOrder(name: String, asc: Boolean)

object HistogramOrder {
  val KEY_ASC    = HistogramOrder("_key", asc = true)
  val KEY_DESC   = HistogramOrder("_key", asc = false)
  val COUNT_ASC  = HistogramOrder("_count", asc = true)
  val COUNT_DESC = HistogramOrder("_count", asc = false)
}
