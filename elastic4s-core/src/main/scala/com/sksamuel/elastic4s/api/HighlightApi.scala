package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.searches.{HighlightField, HighlightOptions}

trait HighlightApi {
  def highlightOptions(): HighlightOptions = HighlightOptions()
  def highlight(field: String): HighlightField = HighlightField(field)
}
