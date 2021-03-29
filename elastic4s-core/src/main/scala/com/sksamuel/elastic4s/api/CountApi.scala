package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.requests.count.CountRequest

trait CountApi {
  def count(indexes: Indexes): CountRequest = CountRequest(indexes)
}
