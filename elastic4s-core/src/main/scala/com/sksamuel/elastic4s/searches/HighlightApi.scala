package com.sksamuel.elastic4s.searches

import scala.language.implicitConversions

case class Highlight(options: HighlightOptionsDefinition,
                     fields: Iterable[HighlightFieldDefinition])

trait HighlightApi {
  def highlightOptions() = HighlightOptionsDefinition()
  def highlight(field: String) = HighlightFieldDefinition(field)
}
