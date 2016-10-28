package com.sksamuel.elastic4s2.search

trait QueryDefinition {
  def builder: org.elasticsearch.index.query.QueryBuilder
}
