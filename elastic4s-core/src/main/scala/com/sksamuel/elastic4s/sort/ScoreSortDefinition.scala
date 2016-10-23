package com.sksamuel.elastic4s.sort

import org.elasticsearch.search.sort.{SortBuilders, SortOrder}

case class ScoreSortDefinition() extends SortDefinition {

  val builder = SortBuilders.scoreSort()

  def order(order: SortOrder) = {
    builder.order(order)
    this
  }
}
