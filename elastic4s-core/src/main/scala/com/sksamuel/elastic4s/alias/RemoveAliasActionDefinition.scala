package com.sksamuel.elastic4s.alias

import com.sksamuel.elastic4s.searches.queries.{QueryDefinition, QueryStringQueryDefinition}
import com.sksamuel.exts.OptionImplicits._

case class RemoveAliasActionDefinition(alias: String,
                                       index: String,
                                       routing: Option[String] = None,
                                       indexRouting: Option[String] = None,
                                       searchRouting: Option[String] = None,
                                       filter: Option[QueryDefinition] = None) extends AliasActionDefinition {
  require(alias.nonEmpty, "alias must not be null or empty")
  require(index.nonEmpty, "index must not be null or empty")

  def withRouting(route: String) = copy(routing = Option(route))
  def withSearchRouting(searchRouting: String) = copy(searchRouting = Option(searchRouting))
  def withIndexRouting(indexRouting: String) = copy(indexRouting = Option(indexRouting))

  def filter(query: String): RemoveAliasActionDefinition = filter(QueryStringQueryDefinition(query))
  def filter(query: QueryDefinition): RemoveAliasActionDefinition = copy(filter = query.some)
}
