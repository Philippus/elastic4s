package com.sksamuel.elastic4s.alias

import com.sksamuel.elastic4s.Executable
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse
import org.elasticsearch.action.admin.indices.alias.{IndicesAliasesRequest, IndicesAliasesResponse}
import org.elasticsearch.client.Client

import scala.concurrent.Future
import scala.language.implicitConversions

trait AliasesDsl {

  class AddAliasExpectsIndex(alias: String) {
    require(alias.nonEmpty, "alias must not be null or empty")
    def on(index: String) = AddAliasActionDefinition(alias, index)
  }

  class RemoveAliasExpectsIndex(alias: String) {
    require(alias.nonEmpty, "alias must not be null or empty")
    def on(index: String) = RemoveAliasActionDefinition(alias, index)
  }

  implicit object GetAliasDefinitionExecutable
    extends Executable[GetAliasDefinition, GetAliasesResponse, GetAliasesResponse] {
    override def apply(c: Client, t: GetAliasDefinition): Future[GetAliasesResponse] = {
      injectFuture(c.admin.indices.getAliases(t.build, _))
    }
  }

  implicit object IndicesAliasesRequestDefinitionExecutable
    extends Executable[IndicesAliasesRequestDefinition, IndicesAliasesResponse, IndicesAliasesResponse] {
    override def apply(c: Client, t: IndicesAliasesRequestDefinition): Future[IndicesAliasesResponse] = {
      injectFuture(c.admin.indices.aliases(t.build, _))
    }
  }

  implicit def getResponseToGetResult(resp: GetAliasesResponse): GetAliasResult = GetAliasResult(resp)
}

trait AliasActionDefinition {
  def build: IndicesAliasesRequest.AliasActions
}

case class IndicesAliasesRequestDefinition(actions: Seq[AliasActionDefinition]) {

  def build: IndicesAliasesRequest = {
    val req = new IndicesAliasesRequest()
    actions.foldLeft(req) { (req, action) =>
      req.addAliasAction(action.build)
    }
    req
  }
}
