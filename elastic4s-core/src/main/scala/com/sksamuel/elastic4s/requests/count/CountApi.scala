package com.sksamuel.elastic4s.requests.count

import com.sksamuel.elastic4s.Indexes

trait CountApi {
  @deprecated(
    "Elasticsearch 6.0 has deprecated types with the intention of removing them in 7.0. You can continue to use them in existing indexes, but all new indexes must only have a single type.",
    "6.0"
  )
  def count(indexes: Indexes, `type`: String) = CountRequest(indexes, Seq(`type`))
  @deprecated(
    "Elasticsearch 6.0 has deprecated types with the intention of removing them in 7.0. You can continue to use them in existing indexes, but all new indexes must only have a single type.",
    "6.0"
  )
  def count(indexes: Indexes, types: Seq[String]) = CountRequest(indexes, types)
  def count(indexes: Indexes)                     = CountRequest(indexes, Nil)
}
