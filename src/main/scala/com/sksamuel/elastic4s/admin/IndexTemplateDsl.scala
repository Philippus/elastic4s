package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.mappings.MappingDefinition
import com.sksamuel.elastic4s.{Executable, ProxyClients}
import org.elasticsearch.action.admin.indices.template.delete.{DeleteIndexTemplateRequest, DeleteIndexTemplateRequestBuilder, DeleteIndexTemplateResponse}
import org.elasticsearch.action.admin.indices.template.put.{PutIndexTemplateRequest, PutIndexTemplateRequestBuilder, PutIndexTemplateResponse}
import org.elasticsearch.client.Client

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

trait TemplateDsl {

  class CreateIndexTemplateExpectsPattern(name: String) {
    def pattern(pat: String) = new CreateIndexTemplateDefinition(name, pat)
  }

  implicit object CreateIndexTemplateDefinitionExecutable
    extends Executable[CreateIndexTemplateDefinition, PutIndexTemplateResponse] {
    override def apply(c: Client, t: CreateIndexTemplateDefinition): Future[PutIndexTemplateResponse] = {
      injectFuture(c.admin.indices.putTemplate(t.build, _))
    }
  }

  implicit object DeleteIndexTemplateDefinitionExecutable
    extends Executable[DeleteIndexTemplateDefinition, DeleteIndexTemplateResponse] {
    override def apply(c: Client, t: DeleteIndexTemplateDefinition): Future[DeleteIndexTemplateResponse] = {
      injectFuture(c.admin.indices.deleteTemplate(t.build, _))
    }
  }
}

class CreateIndexTemplateDefinition(name: String, pattern: String) {

  val _mappings = new ListBuffer[MappingDefinition]
  val _builder = new PutIndexTemplateRequestBuilder(ProxyClients.indices, name).setTemplate(pattern)

  def build: PutIndexTemplateRequest = {
    for ( mapping <- _mappings ) {
      _builder.addMapping(mapping.`type`, mapping.build)
    }
    _builder.request
  }

  def mappings(mappings: MappingDefinition*): this.type = {
    _mappings appendAll mappings.map(_.numericDetection(false).useTtl(false))
    this
  }
}

class DeleteIndexTemplateDefinition(name: String) {
  def build: DeleteIndexTemplateRequest = _builder.request
  val _builder = new DeleteIndexTemplateRequestBuilder(ProxyClients.indices, name)
}
