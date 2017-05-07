package com.sksamuel.elastic4s.alias

import scala.language.implicitConversions

trait AliasesApi {

  def aliases(first: AliasActionDefinition, rest: AliasActionDefinition*): IndicesAliasesRequestDefinition = aliases(first +: rest)
  def aliases(actions: Iterable[AliasActionDefinition]) = IndicesAliasesRequestDefinition(actions.toSeq)

  def addAlias(alias: String) = new AddAliasExpectsOn(alias)
  class AddAliasExpectsOn(alias: String) {
    def on(index: String) = AddAliasActionDefinition(alias, index)
  }

  def removeAlias(alias: String) = new RemoveAliasExpectsOn(alias)
  class RemoveAliasExpectsOn(alias: String) {
    def on(index: String) = RemoveAliasActionDefinition(alias, index)
  }

  def getAliases(): GetAliasesDefinition = GetAliasesDefinition()

  def getAlias(first: String, rest: String*): GetAliasDefinition = GetAliasDefinition(first +: rest)
  def getAlias(aliases: Iterable[String]): GetAliasDefinition = GetAliasDefinition(aliases.toSeq)
}

case class GetAliasesDefinition()
