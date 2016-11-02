package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.ProxyClients
import org.elasticsearch.action.admin.indices.template.get.{GetIndexTemplatesAction, GetIndexTemplatesRequest, GetIndexTemplatesRequestBuilder}

case class GetTemplateDefinition(name: String) {
  def build: GetIndexTemplatesRequest = _builder.request
  val _builder = new GetIndexTemplatesRequestBuilder(ProxyClients.indices, GetIndexTemplatesAction.INSTANCE, name)
}
