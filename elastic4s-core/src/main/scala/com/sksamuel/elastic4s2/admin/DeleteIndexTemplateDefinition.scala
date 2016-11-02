package com.sksamuel.elastic4s2.admin

import com.sksamuel.elastic4s2.ProxyClients
import org.elasticsearch.action.admin.indices.template.delete.{DeleteIndexTemplateAction, DeleteIndexTemplateRequest, DeleteIndexTemplateRequestBuilder}

case class DeleteIndexTemplateDefinition(name: String) {
  def build: DeleteIndexTemplateRequest = _builder.request
  val _builder = new DeleteIndexTemplateRequestBuilder(ProxyClients.indices, DeleteIndexTemplateAction.INSTANCE, name)
}
