package com.sksamuel.elastic4s.requests.count

import com.sksamuel.elastic4s.Indexes

trait CountApi {
  def count(indexes: Indexes)                     = CountRequest(indexes)
}
