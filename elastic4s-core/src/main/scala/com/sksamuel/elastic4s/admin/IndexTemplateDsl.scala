package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.mappings.MappingDefinition
import com.sksamuel.elastic4s.{Executable, ProxyClients}
import org.elasticsearch.action.admin.indices.template.delete.{DeleteIndexTemplateAction, DeleteIndexTemplateRequest, DeleteIndexTemplateRequestBuilder, DeleteIndexTemplateResponse}
import org.elasticsearch.action.admin.indices.template.get.{GetIndexTemplatesAction, GetIndexTemplatesRequest, GetIndexTemplatesRequestBuilder, GetIndexTemplatesResponse}
import org.elasticsearch.action.admin.indices.template.put.{PutIndexTemplateAction, PutIndexTemplateRequest, PutIndexTemplateRequestBuilder, PutIndexTemplateResponse}
import org.elasticsearch.client.Client

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

trait IndexTemplateDsl {

  class CreateIndexTemplateExpectsPattern(name: String) {
    def pattern(pat: String) = new CreateIndexTemplateDefinition(name, pat)
  }

  implicit object CreateIndexTemplateDefinitionExecutable
    extends Executable[CreateIndexTemplateDefinition, PutIndexTemplateResponse, PutIndexTemplateResponse] {
    override def apply(c: Client, t: CreateIndexTemplateDefinition): Future[PutIndexTemplateResponse] = {
      val req = c.admin.indices.preparePutTemplate(t.name).setTemplate(t.pattern)
      t._mappings.foreach(mapping => {
        req.addMapping(mapping.`type`, mapping.buildWithName)
      })
      injectFuture(req.execute)
    }
  }

  implicit object DeleteIndexTemplateDefinitionExecutable
    extends Executable[DeleteIndexTemplateDefinition, DeleteIndexTemplateResponse, DeleteIndexTemplateResponse] {
    override def apply(c: Client, t: DeleteIndexTemplateDefinition): Future[DeleteIndexTemplateResponse] = {
      injectFuture(c.admin.indices.deleteTemplate(t.build, _))
    }
  }

  implicit object GetTemplateDefinitionExecutable
    extends Executable[GetTemplateDefinition, GetIndexTemplatesResponse, GetIndexTemplatesResponse] {
    override def apply(c: Client, t: GetTemplateDefinition): Future[GetIndexTemplatesResponse] = {
      injectFuture(c.admin.indices.getTemplates(t.build, _))
    }
  }
}

class CreateIndexTemplateDefinition(val name: String, val pattern: String) {

  val _mappings = new ListBuffer[MappingDefinition]
  val _builder = new PutIndexTemplateRequestBuilder(ProxyClients.indices, PutIndexTemplateAction.INSTANCE, name)
    .setTemplate(pattern)

  def build: PutIndexTemplateRequest = {
    for ( mapping <- _mappings ) {
      _builder.addMapping(mapping.`type`, mapping.build)
    }
    println(_builder.request().mappings)
    _builder.request
  }

  def mappings(mappings: MappingDefinition*): this.type = {
    _mappings appendAll mappings
    this
  }
}

class DeleteIndexTemplateDefinition(name: String) {
  def build: DeleteIndexTemplateRequest = _builder.request
  val _builder = new DeleteIndexTemplateRequestBuilder(ProxyClients.indices, DeleteIndexTemplateAction.INSTANCE, name)
}

class GetTemplateDefinition(name: String) {
  def build: GetIndexTemplatesRequest = _builder.request
  val _builder = new GetIndexTemplatesRequestBuilder(ProxyClients.indices, GetIndexTemplatesAction.INSTANCE, name)
}
