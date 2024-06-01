package com.sksamuel.elastic4s.handlers.searches.queries

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object NoopQueryBuilderFn {
  def apply(): XContentBuilder = XContentFactory.parse("")
}
