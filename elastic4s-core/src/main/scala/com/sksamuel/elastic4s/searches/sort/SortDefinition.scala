package com.sksamuel.elastic4s.searches.sort

import org.elasticsearch.search.sort.SortBuilder

trait SortDefinition[T <: SortBuilder[T]] {
  def builder: SortBuilder[T]
}
