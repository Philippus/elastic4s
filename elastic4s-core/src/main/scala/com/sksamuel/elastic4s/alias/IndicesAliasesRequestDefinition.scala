package com.sksamuel.elastic4s.alias

import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest

case class IndicesAliasesRequestDefinition(actions: Seq[AliasActionDefinition]) {

  def build: IndicesAliasesRequest = {
    val req = new IndicesAliasesRequest()
    actions.foldLeft(req) { (req, action) =>
      req.addAliasAction(action.build)
    }
    req
  }
}
