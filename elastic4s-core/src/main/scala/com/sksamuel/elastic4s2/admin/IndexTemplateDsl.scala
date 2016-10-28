package com.sksamuel.elastic4s2.admin

import com.sksamuel.elastic4s2.Executable
import org.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateResponse
import org.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesResponse
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateResponse
import org.elasticsearch.client.Client
import org.elasticsearch.common.xcontent.XContentFactory

import scala.concurrent.Future

trait IndexTemplateDsl {

  def deleteTemplate(name: String): DeleteIndexTemplateDefinition = DeleteIndexTemplateDefinition(name)

  def createTemplate(name: String) = new {
    def pattern(pat: String) = CreateIndexTemplateDefinition(name, pat)
  }

  def getTemplate(name: String): GetTemplateDefinition = GetTemplateDefinition(name)

  implicit object CreateIndexTemplateDefinitionExecutable
    extends Executable[CreateIndexTemplateDefinition, PutIndexTemplateResponse, PutIndexTemplateResponse] {
    override def apply(c: Client, t: CreateIndexTemplateDefinition): Future[PutIndexTemplateResponse] = {
      import t._

      val req = c.admin.indices.preparePutTemplate(t.name).setTemplate(t.pattern)
      _mappings.foreach(mapping => {
        req.addMapping(mapping.`type`, mapping.buildWithName)
      })

      if (_settings.settings.nonEmpty || _analysis.nonEmpty) {
        val source = XContentFactory.jsonBuilder().startObject()

        _settings.settings foreach { p => source.field(p._1, p._2) }

        _analysis.foreach(_.build(source))

        source.endObject()

        req.setSettings(source.string())
      }

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
