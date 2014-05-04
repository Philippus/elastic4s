package com.sksamuel.elastic4s

import org.elasticsearch.index.query.FilterBuilder
import org.elasticsearch.cluster.metadata.AliasAction
import org.elasticsearch.action.admin.indices.alias.get.{ GetAliasesAction, GetAliasesRequest }
import org.elasticsearch.action.admin.indices.alias.{ IndicesAliasesRequest, IndicesAliasesAction }

trait AliasesDsl {
  def aliases = new AliasesExpectsAction

  def aliases(aliasMutations: MutateAliasDefinition*) = new IndicesAliasesRequestDefinition(aliasMutations: _*)

  class AliasesExpectsAction {
    def add(alias: String) = new AddAliasExpectsIndex(alias)
    def remove(alias: String) = new RemoveAliasExpectsIndex(alias)
    def get(aliases: String*) = new GetAliasDefinition(aliases)
  }

  class AddAliasExpectsIndex(alias: String) {
    def on(index: String) = new MutateAliasDefinition(new AliasAction(AliasAction.Type.ADD, index, alias))
  }

  class RemoveAliasExpectsIndex(alias: String) {
    def on(index: String) = new MutateAliasDefinition(new AliasAction(AliasAction.Type.REMOVE, index, alias))
  }

  class GetAliasDefinition(aliases: Seq[String])
      extends IndicesRequestDefinition(GetAliasesAction.INSTANCE) {
    val request = new GetAliasesRequest(aliases.toArray)
    def build = request
    def on(indexes: String*): GetAliasDefinition = {
      request.indices(indexes: _*)
      this
    }
  }

  class MutateAliasDefinition(val aliasAction: AliasAction)
      extends IndicesRequestDefinition(IndicesAliasesAction.INSTANCE) {
    def routing(route: String) = new MutateAliasDefinition(aliasAction.routing(route))
    def filter(filter: FilterBuilder) = new MutateAliasDefinition(aliasAction.filter(filter))
    def build = new IndicesAliasesRequest().addAliasAction(aliasAction)
  }

  class IndicesAliasesRequestDefinition(aliasMutations: MutateAliasDefinition*)
      extends IndicesRequestDefinition(IndicesAliasesAction.INSTANCE) {
    def build = aliasMutations.foldLeft(new IndicesAliasesRequest())(
      (request, aliasDef) => request.addAliasAction(aliasDef.aliasAction))
  }
}
