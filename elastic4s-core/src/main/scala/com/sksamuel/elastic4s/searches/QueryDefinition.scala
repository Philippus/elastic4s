package com.sksamuel.elastic4s.searches

trait QueryDefinition {
  def builder: org.elasticsearch.index.query.QueryBuilder
}
