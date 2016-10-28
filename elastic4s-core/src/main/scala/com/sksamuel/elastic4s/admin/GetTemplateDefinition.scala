package com.sksamuel.elastic4s.admin

import org.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesRequest

case class GetTemplateDefinition(name: String) {
  def build: GetIndexTemplatesRequest = _builder.request
  val _builder = new GetIndexTemplatesRequestBuilder(ProxyClients.indices, GetIndexTemplatesAction.INSTANCE, name)
}
