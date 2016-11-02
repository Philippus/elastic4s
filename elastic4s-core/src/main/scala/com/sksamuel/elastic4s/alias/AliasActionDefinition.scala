package com.sksamuel.elastic4s.alias

import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest

trait AliasActionDefinition {
  def build: IndicesAliasesRequest.AliasActions
}
