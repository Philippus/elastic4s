package com.sksamuel.elastic4s.alias

import com.sksamuel.elastic4s.Indexes

import scala.language.implicitConversions

trait AliasesApi {

  def aliases(first: AliasActionDefinition, rest: AliasActionDefinition*): IndicesAliasesRequestDefinition = aliases(first +: rest)
  def aliases(actions: Iterable[AliasActionDefinition]) = IndicesAliasesRequestDefinition(actions.toSeq)

  def addAlias(alias: String, index: String) = AddAliasActionDefinition(alias, index)
  def addAlias(alias: String) = new AddAliasExpectsOn(alias)
  class AddAliasExpectsOn(alias: String) {
    def on(index: String) = AddAliasActionDefinition(alias, index)
  }

  def removeAlias(alias: String, index: String) = RemoveAliasActionDefinition(alias, index)
  def removeAlias(alias: String) = new RemoveAliasExpectsOn(alias)
  class RemoveAliasExpectsOn(alias: String) {
    def on(index: String) = RemoveAliasActionDefinition(alias, index)
  }

  // returns all indexes/aliases
  def getAliases(): GetAliasesDefinition = GetAliasesDefinition(Indexes.All)

  // returns the named aliases for the given indexes
  def getAliases(indexes: Indexes, aliases: Seq[String]): GetAliasesDefinition = GetAliasesDefinition(indexes, aliases)
  def getAliases(index: String, aliases: Seq[String]): GetAliasesDefinition = getAliases(Indexes(index), aliases)
  def getAliases(index: Seq[String], aliases: Seq[String]): GetAliasesDefinition = getAliases(Indexes(index), aliases)

  @deprecated("use getAliases(indexes, aliases), where you can pass in Nil for indexes or aliases to act as a wildcard", "6.0.0")
  def getAlias(first: String, rest: String*): GetAliasesDefinition = GetAliasesDefinition(Nil, first +: rest)

  @deprecated("use getAliases(indexes, aliases), where you can pass in Nil for indexes or aliases to act as a wildcard", "6.0.0")
  def getAlias(aliases: Iterable[String]): GetAliasesDefinition = GetAliasesDefinition(Nil, aliases.toSeq)
}
