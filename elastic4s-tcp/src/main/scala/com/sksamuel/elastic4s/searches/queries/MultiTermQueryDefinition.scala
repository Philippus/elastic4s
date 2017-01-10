package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.index.query.MultiTermQueryBuilder

trait MultiTermQueryDefinition extends QueryDefinition {
  def builder: MultiTermQueryBuilder
}
