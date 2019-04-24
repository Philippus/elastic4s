package com.sksamuel.elastic4s.requests.validate

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class ValidateRequest(indexes: Indexes,
                           query: Query,
                           rewrite: Option[Boolean] = None,
                           explain: Option[Boolean] = None) {
  require(indexes != null, "value must not be null or empty")

  def rewrite(rewrite: Boolean): ValidateRequest = copy(rewrite = rewrite.some)
  def explain(explain: Boolean): ValidateRequest = copy(explain = explain.some)
}
