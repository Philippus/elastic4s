package com.sksamuel.elastic4s.requests.alias

import com.sksamuel.elastic4s.Indexes

import scala.language.implicitConversions

trait AliasesApi {

  def aliases(first: AliasAction, rest: AliasAction*): IndicesAliasesRequest =
    aliases(first +: rest)
  def aliases(actions: Iterable[AliasAction]) = IndicesAliasesRequest(actions.toSeq)

  def addAlias(alias: String, index: String) = AddAliasActionRequest(alias, index)
  def addAlias(alias: String)                = new AddAliasExpectsOn(alias)
  class AddAliasExpectsOn(alias: String) {
    def on(index: String) = AddAliasActionRequest(alias, index)
  }

  def removeAlias(alias: String, index: String) = RemoveAliasAction(alias, index)
  def removeAlias(alias: String)                = new RemoveAliasExpectsOn(alias)
  class RemoveAliasExpectsOn(alias: String) {
    def on(index: String) = RemoveAliasAction(alias, index)
  }

  // returns all indexes/aliases
  def getAliases(): GetAliasesRequest = GetAliasesRequest(Indexes.All)

  // returns the named aliases for the given indexes
  def getAliases(indexes: Indexes, aliases: Seq[String]): GetAliasesRequest   = GetAliasesRequest(indexes, aliases)
  def getAliases(index: String, aliases: Seq[String]): GetAliasesRequest      = getAliases(Indexes(index), aliases)
  def getAliases(index: Seq[String], aliases: Seq[String]): GetAliasesRequest = getAliases(Indexes(index), aliases)

  @deprecated("use getAliases(indexes, aliases), where you can pass in Nil for indexes or aliases to act as a wildcard",
              "6.0.0")
  def getAlias(first: String, rest: String*): GetAliasesRequest = GetAliasesRequest(Nil, first +: rest)

  @deprecated("use getAliases(indexes, aliases), where you can pass in Nil for indexes or aliases to act as a wildcard",
              "6.0.0")
  def getAlias(aliases: Iterable[String]): GetAliasesRequest = GetAliasesRequest(Nil, aliases.toSeq)
}
