package com.sksamuel.elastic4s.search

trait QueryDefinition {
  def builder: org.elasticsearch.index.query.QueryBuilder
}
