package com.sksamuel.elastic4s.requests.alias

import com.sksamuel.elastic4s.requests.searches.queries.{Query, QueryStringQuery}
import com.sksamuel.exts.OptionImplicits._

case class AddAliasActionRequest(alias: String,
                                 index: String,
                                 routing: Option[String] = None,
                                 indexRouting: Option[String] = None,
                                 searchRouting: Option[String] = None,
                                 filter: Option[Query] = None,
                                 isWriteIndex: Option[Boolean] = None)
    extends AliasAction {
  require(alias.nonEmpty, "alias must not be null or empty")
  require(index.nonEmpty, "index must not be null or empty")

  def routing(route: String): AddAliasActionRequest               = copy(routing = Option(route))
  def searchRouting(searchRouting: String): AddAliasActionRequest = copy(searchRouting = Option(searchRouting))
  def indexRouting(indexRouting: String): AddAliasActionRequest   = copy(indexRouting = Option(indexRouting))

  def filter(query: String): AddAliasActionRequest = filter(QueryStringQuery(query))
  def filter(query: Query): AddAliasActionRequest  = copy(filter = query.some)

  def isWriteIndex(isWriteIndex: Boolean): AddAliasActionRequest =          this.isWriteIndex(isWriteIndex.some)
  def isWriteIndex(isWriteIndex: Option[Boolean]): AddAliasActionRequest =  copy(isWriteIndex = isWriteIndex)
}
