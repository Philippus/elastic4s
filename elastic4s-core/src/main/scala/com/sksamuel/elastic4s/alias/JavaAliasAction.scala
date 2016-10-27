package com.sksamuel.elastic4s.alias

import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest

case class JavaAliasAction(action: IndicesAliasesRequest.AliasActions) extends AliasActionDefinition {
  override def build: IndicesAliasesRequest.AliasActions = action
}
