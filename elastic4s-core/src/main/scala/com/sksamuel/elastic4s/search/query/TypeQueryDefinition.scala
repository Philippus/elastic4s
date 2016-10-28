package com.sksamuel.elastic4s.search.query

import com.sksamuel.elastic4s.search.QueryDefinition
import org.elasticsearch.index.query.QueryBuilders

case class TypeQueryDefinition(`type`: String) extends QueryDefinition {
  val builder = QueryBuilders.typeQuery(`type`)
}
