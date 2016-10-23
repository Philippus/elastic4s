package com.sksamuel.elastic4s.search

/** @author Stephen Samuel */
trait HighlightDsl {

  implicit def string2highlightfield(name: String): HighlightDefinition = HighlightDefinition(name)

  def options = new HighlightOptionsDefinition
}
