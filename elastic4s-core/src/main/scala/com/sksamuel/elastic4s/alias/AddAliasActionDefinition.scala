package com.sksamuel.elastic4s.alias

import com.sksamuel.elastic4s.searches.queries.{QueryDefinition, QueryStringQueryDefinition}
import com.sksamuel.exts.OptionImplicits._

case class AddAliasActionDefinition(alias: String,
                                    index: String,
                                    routing: Option[String] = None,
                                    indexRouting: Option[String] = None,
                                    searchRouting: Option[String] = None,
                                    filter: Option[QueryDefinition] = None)
    extends AliasActionDefinition {
  require(alias.nonEmpty, "alias must not be null or empty")
  require(index.nonEmpty, "index must not be null or empty")

  def routing(route: String): AddAliasActionDefinition               = copy(routing = Option(route))
  def searchRouting(searchRouting: String): AddAliasActionDefinition = copy(searchRouting = Option(searchRouting))
  def indexRouting(indexRouting: String): AddAliasActionDefinition   = copy(indexRouting = Option(indexRouting))

  def filter(query: String): AddAliasActionDefinition          = filter(QueryStringQueryDefinition(query))
  def filter(query: QueryDefinition): AddAliasActionDefinition = copy(filter = query.some)
}
