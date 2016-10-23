package com.sksamuel.elastic4s.queries

import org.elasticsearch.index.query.MultiTermQueryBuilder

trait MultiTermQueryDefinition extends QueryDefinition {
  override def builder: MultiTermQueryBuilder
}
