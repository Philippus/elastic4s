package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.json.XContentBuilder

trait CustomQuery extends Query {
  def buildQueryBody() : XContentBuilder
}
