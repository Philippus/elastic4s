package com.sksamuel.elastic4s2.admin

import com.sksamuel.elastic4s2.ProxyClients
import org.elasticsearch.action.admin.indices.template.get.{GetIndexTemplatesAction, GetIndexTemplatesRequest, GetIndexTemplatesRequestBuilder}

case class GetTemplateDefinition(name: String) {
  def build: GetIndexTemplatesRequest = _builder.request
  val _builder = new GetIndexTemplatesRequestBuilder(ProxyClients.indices, GetIndexTemplatesAction.INSTANCE, name)
}
