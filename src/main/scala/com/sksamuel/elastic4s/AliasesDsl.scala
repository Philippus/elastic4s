package com.sksamuel.elastic4s

import org.elasticsearch.index.query.FilterBuilder
import org.elasticsearch.cluster.metadata.AliasAction
import org.elasticsearch.action.admin.indices.alias.get.{ GetAliasesAction, GetAliasesRequest }
import org.elasticsearch.action.admin.indices.alias.{ IndicesAliasesRequest, IndicesAliasesAction }

trait AliasesDsl {
  def aliases = new AliasesExpectsAction

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

  class GetAliasExpectsIndex(aliases: Seq[String]) {
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

  class MutateAliasDefinition(aliasAction: AliasAction)
      extends IndicesRequestDefinition(IndicesAliasesAction.INSTANCE) {
    def routing(route: String) = new MutateAliasDefinition(aliasAction.routing(route))
    def filter(filter: FilterBuilder) = new MutateAliasDefinition(aliasAction.filter(filter))
    def build = new IndicesAliasesRequest().addAliasAction(aliasAction)
  }
}
