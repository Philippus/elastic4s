package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.highlighting.HighlightFieldDefinition
import org.elasticsearch.index.query.InnerHitBuilder

case class InnerHitDefinition(name: String,
                              highlight: Option[HighlightFieldDefinition] = None) {

  def builder = {
    val builder = new InnerHitBuilder().setName(name)
    // todo
    // highlight.foreach(highlight => builder.setHighlightBuilder(highlight.builder))
    builder
  }

  def highlighting(highlight: HighlightFieldDefinition) = copy(highlight = Some(highlight))
}
