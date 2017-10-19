package com.sksamuel.elastic4s.count

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

case class CountDefinition(indexes: Indexes, types: Seq[String], query: Option[QueryDefinition] = None) {
  def filter(query: QueryDefinition): CountDefinition = copy(query = query.some)
}
