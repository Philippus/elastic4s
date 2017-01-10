package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.Executable
import org.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateResponse
import org.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesResponse
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait IndexTemplateDsl {

  def deleteTemplate(name: String): DeleteIndexTemplateDefinition = DeleteIndexTemplateDefinition(name)

  def createTemplate(name: String) = new CreateIndexTemplateExpectsPattern(name)
  class CreateIndexTemplateExpectsPattern(name: String) {
    def pattern(pat: String) = CreateIndexTemplateDefinition(name, pat)
  }

  def getTemplate(name: String): GetTemplateDefinition = GetTemplateDefinition(name)

  implicit object CreateIndexTemplateDefinitionExecutable
    extends Executable[CreateIndexTemplateDefinition, PutIndexTemplateResponse, PutIndexTemplateResponse] {
    override def apply(c: Client, t: CreateIndexTemplateDefinition): Future[PutIndexTemplateResponse] = {
      val builder = c.admin.indices.preparePutTemplate(t.name)
      t.populate(builder)
      injectFuture(builder.execute)
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
