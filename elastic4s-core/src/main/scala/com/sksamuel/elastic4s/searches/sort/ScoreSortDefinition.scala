package com.sksamuel.elastic4s.searches.sort

import org.elasticsearch.search.sort.{ScoreSortBuilder, SortBuilders, SortOrder}

case class ScoreSortDefinition() extends SortDefinition[ScoreSortBuilder] {

  val builder = SortBuilders.scoreSort()

  def order(order: SortOrder) = {
    builder.order(order)
    this
  }
}
