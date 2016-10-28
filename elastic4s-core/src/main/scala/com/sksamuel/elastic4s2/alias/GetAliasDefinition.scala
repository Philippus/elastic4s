package com.sksamuel.elastic4s2.alias

import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest

case class GetAliasDefinition(aliases: Seq[String]) {

  val request = new GetAliasesRequest(aliases.toArray)
  def build = request

  def on(indexes: String*): GetAliasDefinition = {
    request.indices(indexes: _*)
    this
  }
}
