package com.sksamuel.elastic4s.queries

import com.sksamuel.elastic4s.search.HighlightDefinition

case class InnerHitDefinition(private[elastic4s] val name: String) {

  private[elastic4s] val inner = new InnerHit

  def path(p: String): this.type = {
    inner.setPath(p)
    this
  }

  def `type`(t: String): this.type = {
    inner.setType(t)
    this
  }

  def highlighting(highlights: HighlightDefinition*): this.type = {
    highlights.foreach(highlight => inner.addHighlightedField(highlight.builder))
    this
  }
}
