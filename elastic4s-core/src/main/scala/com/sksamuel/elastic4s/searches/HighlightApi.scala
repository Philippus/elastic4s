package com.sksamuel.elastic4s.searches

import scala.language.implicitConversions

case class Highlight(options: HighlightOptionsDefinition,
                     fields: Iterable[HighlightFieldDefinition])

trait HighlightApi {

  @deprecated("use highlight(name)", "5.0.0")
  implicit def string2highlightfield(name: String): HighlightFieldDefinition = HighlightFieldDefinition(name)

  @deprecated("use highlightOptions()", "5.0.0")
  def options = HighlightOptionsDefinition()

  def highlightOptions() = HighlightOptionsDefinition()

  def highlight(field: String) = HighlightFieldDefinition(field)
}
