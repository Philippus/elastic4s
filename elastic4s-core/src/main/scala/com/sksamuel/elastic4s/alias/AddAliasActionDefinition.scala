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

  def withRouting(route: String) = copy(routing = Option(route))
  def withSearchRouting(searchRouting: String) = copy(searchRouting = Option(searchRouting))
  def withIndexRouting(indexRouting: String) = copy(indexRouting = Option(indexRouting))

  def withFilter(query: String): AddAliasActionDefinition = withFilter(QueryStringQueryDefinition(query))
  def withFilter(query: QueryBuilder): AddAliasActionDefinition = copy(filter = Option(query))
  def withFilter(query: QueryDefinition): AddAliasActionDefinition = withFilter(query.builder)

  override def build: IndicesAliasesRequest.AliasActions = {
    val action = AliasActions.add().alias(alias).index(index)
    routing.foreach(action.routing)
    indexRouting.foreach(action.indexRouting)
    searchRouting.foreach(action.searchRouting)
    searchRouting.foreach(action.filter)
    action
  }
}
