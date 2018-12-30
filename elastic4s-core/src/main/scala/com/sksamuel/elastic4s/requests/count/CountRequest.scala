package com.sksamuel.elastic4s.requests.count

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class CountRequest(indexes: Indexes, types: Seq[String], query: Option[Query] = None) {
  def filter(query: Query): CountRequest = copy(query = query.some)
  def query(query: Query): CountRequest  = filter(query)
}
