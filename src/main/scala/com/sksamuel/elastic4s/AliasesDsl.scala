package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.alias.{IndicesAliasesAction, IndicesAliasesRequest}
import org.elasticsearch.index.query.FilterBuilder
import org.elasticsearch.cluster.metadata.AliasAction

trait AliasesDsl {
  def aliases = new AliasesExpectsAction

  class AliasesExpectsAction {
    def add(alias: String) = new AddAliasExpectsIndex(alias)
    def remove(alias: String) = new RemoveAliasExpectsIndex(alias)
  }

  class AddAliasExpectsIndex(alias: String) {
    def on(index: String) = new AliasDefinition(new AliasAction(AliasAction.Type.ADD, index, alias))
  }

  class RemoveAliasExpectsIndex(alias: String) {
    def on(index: String) = new AliasDefinition(new AliasAction(AliasAction.Type.REMOVE, index, alias))
  }

  class AliasDefinition(aliasAction: AliasAction) extends IndicesRequestDefinition(IndicesAliasesAction.INSTANCE) {
    def routing(route: String) = new AliasDefinition(aliasAction.routing(route))

    def filter(filter: FilterBuilder) = new AliasDefinition(aliasAction.filter(filter))

    def build = new IndicesAliasesRequest().addAliasAction(aliasAction)
  }
}
