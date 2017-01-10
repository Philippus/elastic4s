package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.ProxyClients
import org.elasticsearch.action.admin.indices.template.delete.{DeleteIndexTemplateAction, DeleteIndexTemplateRequest, DeleteIndexTemplateRequestBuilder}

case class DeleteIndexTemplateDefinition(name: String) {
  def build: DeleteIndexTemplateRequest = _builder.request
  val _builder = new DeleteIndexTemplateRequestBuilder(ProxyClients.indices, DeleteIndexTemplateAction.INSTANCE, name)
}
