package com.sksamuel.elastic4s.index

import com.sksamuel.elastic4s.Executable
import com.sksamuel.elastic4s.admin.CreateIndexTemplateBuilder
import com.sksamuel.elastic4s.indexes.{CreateIndexTemplateDefinition, DeleteIndexTemplateDefinition, GetIndexTemplateDefinition}
import org.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateResponse
import org.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesResponse
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait IndexTemplateExecutables {

  implicit object CreateIndexTemplateExecutable
    extends Executable[CreateIndexTemplateDefinition, PutIndexTemplateResponse, PutIndexTemplateResponse] {
    override def apply(c: Client, t: CreateIndexTemplateDefinition): Future[PutIndexTemplateResponse] = {
      val builder = c.admin.indices.preparePutTemplate(t.name)
      CreateIndexTemplateBuilder(builder, t)
      injectFuture(builder.execute(_))
    }
  }

  implicit object DeleteIndexTemplateExecutable
    extends Executable[DeleteIndexTemplateDefinition, DeleteIndexTemplateResponse, DeleteIndexTemplateResponse] {
    override def apply(c: Client, t: DeleteIndexTemplateDefinition): Future[DeleteIndexTemplateResponse] = {
      val builder = c.admin().indices().prepareDeleteTemplate(t.name)
      injectFuture(builder.execute(_))
    }
  }

  implicit object GetTemplateExecutable
    extends Executable[GetIndexTemplateDefinition, GetIndexTemplatesResponse, GetIndexTemplatesResponse] {
    override def apply(c: Client, t: GetIndexTemplateDefinition): Future[GetIndexTemplatesResponse] = {
      val builder = c.admin().indices().prepareGetTemplates(t.name)
      injectFuture(builder.execute(_))
    }
  }
}
