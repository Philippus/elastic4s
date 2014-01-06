package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.alias.{IndicesAliasesAction, IndicesAliasesRequest}
import org.elasticsearch.index.query.FilterBuilder

trait AddAliasDsl {
  def addAlias(index: String, alias: String) = new AddAliasDefinition(index: String, alias: String)
  def addAlias(index: String, alias: String, filters: FilterBuilder) = new AddAliasDefinition(index: String, alias: String, filters)

  class AddAliasDefinition(index: String, alias: String, filters: FilterBuilder) extends IndicesRequestDefinition(IndicesAliasesAction.INSTANCE) {
    def this(index: String, alias: String) = this(index, alias, null)
    private val builder = new IndicesAliasesRequest().addAlias(index, alias, filters)
    def build = builder
  }
}

trait RemoveAliasDsl {
  def removeAlias(index: String, alias: String) = new RemoveAliasDefinition(index: String, alias: String)

  class RemoveAliasDefinition(index: String, alias: String) extends IndicesRequestDefinition(IndicesAliasesAction.INSTANCE) {
    private val builder = new IndicesAliasesRequest().removeAlias(index, alias)
    def build = builder
  }
}

