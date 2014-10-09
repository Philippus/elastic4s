package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.mappings.MappingDefinition
import org.elasticsearch.action.admin.indices.template.delete.{DeleteIndexTemplateRequest, DeleteIndexTemplateRequestBuilder}
import org.elasticsearch.action.admin.indices.template.put.{PutIndexTemplateRequest, PutIndexTemplateRequestBuilder}

import scala.collection.mutable.ListBuffer

trait IndexTemplateDsl {
  def template = TemplateExpectsCreateOrDelete
  object TemplateExpectsCreateOrDelete {
    def create(name: String) = new CreateIndexTemplateDefinition(name)
    def delete(name: String) = new DeleteIndexTemplateDefinition(name)
  }
}

class CreateIndexTemplateDefinition(name: String) {

  val _mappings = new ListBuffer[MappingDefinition]
  val _builder = new PutIndexTemplateRequestBuilder(ProxyClients.indices, name)

  def build: PutIndexTemplateRequest = _builder.request

  def pattern(pattern: String): this.type = {
    _builder.setTemplate(pattern)
    this
  }

  def mappings(mappings: MappingDefinition*): this.type = {
    for ( mapping <- mappings ) {
      _builder.addMapping("", mapping.build)
    }
    this
  }
}

class DeleteIndexTemplateDefinition(name: String) {
  def build: DeleteIndexTemplateRequest = _builder.request
  val _builder = new DeleteIndexTemplateRequestBuilder(ProxyClients.indices, name)
}
