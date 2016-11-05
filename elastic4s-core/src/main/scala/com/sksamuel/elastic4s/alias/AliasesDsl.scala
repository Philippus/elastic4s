package com.sksamuel.elastic4s.alias

import com.sksamuel.elastic4s.Executable
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future
import scala.language.implicitConversions

trait AliasesDsl {

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

  def getAlias(first: String, rest: String*): GetAliasDefinition = GetAliasDefinition(first +: rest)
  def getAlias(aliases: Iterable[String]): GetAliasDefinition = GetAliasDefinition(aliases.toSeq)

  implicit object GetAliasDefinitionExecutable
    extends Executable[GetAliasDefinition, GetAliasesResponse, GetAliasesResponse] {
    override def apply(c: Client, t: GetAliasDefinition): Future[GetAliasesResponse] = {
      injectFuture(c.admin.indices.getAliases(t.build, _))
    }
  }

  // executable for a bulk alias definition
  implicit object IndicesAliasesRequestDefinitionExecutable
    extends Executable[IndicesAliasesRequestDefinition, IndicesAliasesResponse, IndicesAliasesResponse] {
    override def apply(c: Client, t: IndicesAliasesRequestDefinition): Future[IndicesAliasesResponse] = {
      injectFuture(c.admin.indices.aliases(t.build, _))
    }
  }

  // executable so we can use addAlias directly
  implicit object AddAliasActionDefinitionExecutable
    extends Executable[AddAliasActionDefinition, IndicesAliasesResponse, IndicesAliasesResponse] {
    override def apply(c: Client, t: AddAliasActionDefinition): Future[IndicesAliasesResponse] = {
      val f = c.admin.indices().prepareAliases().addAliasAction(t.build).execute()
      injectFuture(f)
    }
  }

  // executable so we can use removeAlias directly
  implicit object RemoveAliasActionDefinitionExecutable
    extends Executable[RemoveAliasActionDefinition, IndicesAliasesResponse, IndicesAliasesResponse] {
    override def apply(c: Client, t: RemoveAliasActionDefinition): Future[IndicesAliasesResponse] = {
      val f = c.admin.indices().prepareAliases().addAliasAction(t.build).execute()
      injectFuture(f)
    }
  }



  implicit def getResponseToGetResult(resp: GetAliasesResponse): GetAliasResult = GetAliasResult(resp)
}