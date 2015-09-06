package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.alias.get.{GetAliasesRequest, GetAliasesResponse}
import org.elasticsearch.action.admin.indices.alias.{IndicesAliasesRequest, IndicesAliasesResponse}
import org.elasticsearch.client.Client
import org.elasticsearch.cluster.metadata.{AliasAction, AliasMetaData}
import org.elasticsearch.index.query.QueryBuilder

import scala.concurrent.Future
import scala.language.implicitConversions

trait AliasesDsl {

  class AddAliasExpectsIndex(alias: String) {
    def on(index: String) = new MutateAliasDefinition(new AliasAction(AliasAction.Type.ADD, index, alias))
  }

  class RemoveAliasExpectsIndex(alias: String) {
    def on(index: String) = new MutateAliasDefinition(new AliasAction(AliasAction.Type.REMOVE, index, alias))
  }

  implicit object GetAliasDefinitionExecutable
    extends Executable[GetAliasDefinition, GetAliasesResponse, GetAliasesResponse] {
    override def apply(c: Client, t: GetAliasDefinition): Future[GetAliasesResponse] = {
      injectFuture(c.admin.indices.getAliases(t.build, _))
    }
  }

  implicit object MutateAliasDefinitionExecutable
    extends Executable[MutateAliasDefinition, IndicesAliasesResponse, IndicesAliasesResponse] {
    override def apply(c: Client, t: MutateAliasDefinition): Future[IndicesAliasesResponse] = {
      injectFuture(c.admin.indices.aliases(t.build, _))
    }
  }

  implicit object IndicesAliasesRequestDefinitionExecutable
    extends Executable[IndicesAliasesRequestDefinition, IndicesAliasesResponse, IndicesAliasesResponse] {
    override def apply(c: Client, t: IndicesAliasesRequestDefinition): Future[IndicesAliasesResponse] = {
      injectFuture(c.admin.indices.aliases(t.build, _))
    }
  }

  implicit def getResponseToGetResult(resp: GetAliasesResponse): GetAliasResult = new GetAliasResult(resp)
}

class GetAliasDefinition(aliases: Seq[String]) {

  val request = new GetAliasesRequest(aliases.toArray)
  def build = request

  def on(indexes: String*): GetAliasDefinition = {
    request.indices(indexes: _*)
    this
  }
}

case class GetAliasResult(response: GetAliasesResponse) {

  import scala.collection.JavaConverters._

  def aliases: Map[String, Seq[AliasMetaData]] = {
    response.getAliases.keysIt().asScala.map(key => key -> response.getAliases.get(key).asScala.toSeq).toMap
  }
}

class MutateAliasDefinition(val aliasAction: AliasAction) {

  def routing(route: String): MutateAliasDefinition = new MutateAliasDefinition(aliasAction.routing(route))

  def filter(filter: QueryBuilder): MutateAliasDefinition = new MutateAliasDefinition(aliasAction.filter(filter))
  def filter(filter: QueryDefinition): MutateAliasDefinition = {
    new MutateAliasDefinition(aliasAction.filter(filter.builder))
  }

  def build: IndicesAliasesRequest = new IndicesAliasesRequest().addAliasAction(aliasAction)
}

class IndicesAliasesRequestDefinition(aliasMutations: MutateAliasDefinition*) {

  def build: IndicesAliasesRequest = {
    aliasMutations.foldLeft(new IndicesAliasesRequest())((request, aliasDef) =>
      request.addAliasAction(aliasDef.aliasAction)
    )
  }
}