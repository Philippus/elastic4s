package com.sksamuel.elastic4s.requests.searches

import scala.language.implicitConversions

case class Highlight(options: HighlightOptions, fields: Iterable[HighlightField])

trait HighlightApi {
  def highlightOptions()       = HighlightOptions()
  def highlight(field: String) = HighlightField(field)
}
