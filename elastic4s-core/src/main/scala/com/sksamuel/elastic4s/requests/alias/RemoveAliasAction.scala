package com.sksamuel.elastic4s.requests.alias

import com.sksamuel.elastic4s.requests.searches.queries.{Query, QueryStringQuery}
import com.sksamuel.exts.OptionImplicits._

case class RemoveAliasAction(alias: String,
                             index: String,
                             routing: Option[String] = None,
                             indexRouting: Option[String] = None,
                             searchRouting: Option[String] = None,
                             filter: Option[Query] = None)
    extends AliasAction {
  require(alias.nonEmpty, "alias must not be null or empty")
  require(index.nonEmpty, "index must not be null or empty")

  def withRouting(route: String): RemoveAliasAction = copy(routing = Option(route))
  def withSearchRouting(searchRouting: String): RemoveAliasAction =
    copy(searchRouting = Option(searchRouting))
  def withIndexRouting(indexRouting: String): RemoveAliasAction = copy(indexRouting = Option(indexRouting))

  def filter(query: String): RemoveAliasAction = filter(QueryStringQuery(query))
  def filter(query: Query): RemoveAliasAction  = copy(filter = query.some)
}
