package com.sksamuel.elastic4s.requests.validate

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits.RichOptionImplicits

case class ValidateRequest(indexes: Indexes,
                           query: Query,
                           rewrite: Option[Boolean] = None,
                           lenient: Option[Boolean] = None,
                           analyzeWildcard: Option[Boolean] = None,
                           ignoreUnavailable: Option[Boolean] = None,
                           explain: Option[Boolean] = None) {
  require(indexes != null, "value must not be null or empty")

  def rewrite(rewrite: Boolean): ValidateRequest = copy(rewrite = rewrite.some)
  def explain(explain: Boolean): ValidateRequest = copy(explain = explain.some)
  def lenient(lenient: Boolean): ValidateRequest = copy(lenient = lenient.some)
  def ignoreUnavailable(ignore: Boolean): ValidateRequest = copy(ignoreUnavailable = ignore.some)
  def analyzeWildcard(analyze: Boolean): ValidateRequest = copy(analyzeWildcard = analyze.some)
}
