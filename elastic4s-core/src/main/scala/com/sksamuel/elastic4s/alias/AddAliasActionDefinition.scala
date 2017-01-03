package com.sksamuel.elastic4s.alias

import com.sksamuel.elastic4s.searches.QueryDefinition
import com.sksamuel.elastic4s.searches.queries.QueryStringQueryDefinition
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest.AliasActions
import org.elasticsearch.index.query.QueryBuilder

case class AddAliasActionDefinition(alias: String,
                                    index: String,
                                    routing: Option[String] = None,
                                    indexRouting: Option[String] = None,
                                    searchRouting: Option[String] = None,
                                    filter: Option[QueryBuilder] = None) extends AliasActionDefinition {
  require(alias.nonEmpty, "alias must not be null or empty")
  require(index.nonEmpty, "index must not be null or empty")

  def routing(route: String): AddAliasActionDefinition = copy(routing = Option(route))
  def searchRouting(searchRouting: String): AddAliasActionDefinition = copy(searchRouting = Option(searchRouting))
  def indexRouting(indexRouting: String): AddAliasActionDefinition = copy(indexRouting = Option(indexRouting))

  def filter(query: String): AddAliasActionDefinition = filter(QueryStringQueryDefinition(query))
  def filter(query: QueryDefinition): AddAliasActionDefinition = filter(query.builder)
  def filter(query: QueryBuilder): AddAliasActionDefinition = copy(filter = Option(query))

  override def build: IndicesAliasesRequest.AliasActions = {
    val action = AliasActions.add().alias(alias).index(index)
    routing.foreach(action.routing)
    indexRouting.foreach(action.indexRouting)
    searchRouting.foreach(action.searchRouting)
    filter.foreach(action.filter)
    action
  }
}
