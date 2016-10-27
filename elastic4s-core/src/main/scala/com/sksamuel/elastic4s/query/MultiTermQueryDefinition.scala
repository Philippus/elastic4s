package com.sksamuel.elastic4s.query

import com.sksamuel.elastic4s.QueryDefinition
import org.elasticsearch.index.query.MultiTermQueryBuilder

trait MultiTermQueryDefinition extends QueryDefinition {
  override def builder: MultiTermQueryBuilder
}
