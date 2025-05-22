package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.requests.alias.{
  AddAliasActionRequest,
  AliasAction,
  GetAliasesRequest,
  IndicesAliasesRequest,
  RemoveAliasAction
}

trait AliasesApi {

  def aliases(first: AliasAction, rest: AliasAction*): IndicesAliasesRequest = aliases(first +: rest)
  def aliases(actions: Iterable[AliasAction]): IndicesAliasesRequest         = IndicesAliasesRequest(actions.toSeq)

  def addAlias(alias: String, index: String): AddAliasActionRequest = AddAliasActionRequest(alias, index)

  def removeAlias(alias: String, index: String): RemoveAliasAction = RemoveAliasAction(alias, index)

  // returns all indexes/aliases
  def getAliases(): GetAliasesRequest = GetAliasesRequest(Indexes.All)

  // returns the named aliases for the given indexes
  def getAliases(indexes: Indexes, aliases: Seq[String]): GetAliasesRequest   = GetAliasesRequest(indexes, aliases)
  def getAliases(index: String, aliases: Seq[String]): GetAliasesRequest      = getAliases(Indexes(index), aliases)
  def getAliases(index: Seq[String], aliases: Seq[String]): GetAliasesRequest = getAliases(Indexes(index), aliases)
}
