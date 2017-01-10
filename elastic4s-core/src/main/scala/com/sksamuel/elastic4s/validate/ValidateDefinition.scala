package com.sksamuel.elastic4s.validate

import com.sksamuel.elastic4s.IndexesAndTypes
import com.sksamuel.elastic4s.searches.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

case class ValidateDefinition(indexesAndTypes: IndexesAndTypes,
                              query: QueryDefinition,
                              rewrite: Option[Boolean] = None,
                              explain: Option[Boolean] = None) {
  require(indexesAndTypes != null, "value must not be null or empty")
  def rewrite(rewrite: Boolean): ValidateDefinition = copy(rewrite = rewrite.some)
  def explain(explain: Boolean): ValidateDefinition = copy(explain = explain.some)
}
