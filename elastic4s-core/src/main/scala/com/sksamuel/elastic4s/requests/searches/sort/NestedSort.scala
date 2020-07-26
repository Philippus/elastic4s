package com.sksamuel.elastic4s.requests.searches.sort

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class NestedSort(path: Option[String] = None,
                      filter: Option[Query] = None,
                      maxChildren: Option[Int] = None,
                      nested: Option[NestedSort] = None) {
  def path(path: String): NestedSort = copy(path = path.some)
  def filter(query: Query): NestedSort = copy(filter = query.some)
  def maxChildren(maxChildren: Int): NestedSort = copy(maxChildren = maxChildren.some)
  def nested(nested: NestedSort): NestedSort = copy(nested = nested.some)
}
