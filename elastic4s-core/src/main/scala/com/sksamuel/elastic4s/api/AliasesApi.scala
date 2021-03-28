package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.requests.alias.{AddAliasActionRequest, AliasAction, GetAliasesRequest, IndicesAliasesRequest, RemoveAliasAction}

trait AliasesApi {

  def aliases(first: AliasAction, rest: AliasAction*): IndicesAliasesRequest = aliases(first +: rest)
  def aliases(actions: Iterable[AliasAction]) = IndicesAliasesRequest(actions.toSeq)

  def addAlias(alias: String, index: String) = AddAliasActionRequest(alias, index)

  @deprecated("use addAlias(alias, index)", "7.7.0")
  def addAlias(alias: String)                = new AddAliasExpectsOn(alias)
  @deprecated("use addAlias(alias, index)", "7.7.0")
  class AddAliasExpectsOn(alias: String) {
    @deprecated("use addAlias(alias, index)", "7.7.0")
    def on(index: String) = AddAliasActionRequest(alias, index)
  }

  def removeAlias(alias: String, index: String) = RemoveAliasAction(alias, index)
  @deprecated("use removeAlias(alias, index)", "7.7.0")
  def removeAlias(alias: String)                = new RemoveAliasExpectsOn(alias)
  @deprecated("use removeAlias(alias, index)", "7.7.0")
  class RemoveAliasExpectsOn(alias: String) {
    @deprecated("use removeAlias(alias, index)", "7.7.0")
    def on(index: String) = RemoveAliasAction(alias, index)
  }

  // returns all indexes/aliases
  def getAliases(): GetAliasesRequest = GetAliasesRequest(Indexes.All)

  // returns the named aliases for the given indexes
  def getAliases(indexes: Indexes, aliases: Seq[String]): GetAliasesRequest   = GetAliasesRequest(indexes, aliases)
  def getAliases(index: String, aliases: Seq[String]): GetAliasesRequest      = getAliases(Indexes(index), aliases)
  def getAliases(index: Seq[String], aliases: Seq[String]): GetAliasesRequest = getAliases(Indexes(index), aliases)
}
