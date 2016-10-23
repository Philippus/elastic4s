package com.sksamuel.elastic4s.sort

import org.elasticsearch.search.sort.SortBuilder

trait SortDefinition {
  type T <: SortBuilder[T]
  def builder: SortBuilder[T]
}
