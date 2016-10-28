package com.sksamuel.elastic4s.search.query

import com.sksamuel.elastic4s.search.QueryDefinition
import org.elasticsearch.index.query.MultiTermQueryBuilder

trait MultiTermQueryDefinition extends QueryDefinition {
  override def builder: MultiTermQueryBuilder
}
