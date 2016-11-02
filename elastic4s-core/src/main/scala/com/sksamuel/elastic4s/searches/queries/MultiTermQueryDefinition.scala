package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.QueryDefinition
import org.elasticsearch.index.query.MultiTermQueryBuilder

trait MultiTermQueryDefinition extends QueryDefinition {
  override def builder: MultiTermQueryBuilder
}
