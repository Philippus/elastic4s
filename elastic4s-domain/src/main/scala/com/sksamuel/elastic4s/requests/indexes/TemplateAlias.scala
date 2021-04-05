package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits.RichOptionImplicits

case class TemplateAlias(name: String, filter: Option[Query] = None, routing: Option[String] = None) {
  def filter(filter: Query): TemplateAlias = copy(filter = filter.some)
  def routing(routing: String): TemplateAlias = copy(routing = routing.some)
}
