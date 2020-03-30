package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.json.XContentBuilder

trait Query

object NoopQuery extends Query

trait MultiTermQuery extends Query

trait CustomQuery extends Query {
  def buildQueryBody() : XContentBuilder
}
