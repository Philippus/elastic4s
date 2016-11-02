package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.QueryDefinition
import org.elasticsearch.index.query.QueryBuilders

case class TypeQueryDefinition(`type`: String) extends QueryDefinition {
  val builder = QueryBuilders.typeQuery(`type`)
}
