package com.sksamuel.elastic4s2.alias

import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest

case class JavaAliasAction(action: IndicesAliasesRequest.AliasActions) extends AliasActionDefinition {
  override def build: IndicesAliasesRequest.AliasActions = action
}
