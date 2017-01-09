package com.sksamuel.elastic4s.alias

import com.sksamuel.elastic4s.Executable
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait AliasExecutables {

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
