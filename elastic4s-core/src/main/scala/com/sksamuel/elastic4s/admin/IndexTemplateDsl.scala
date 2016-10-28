package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.analyzers.AnalyzerDefinition
import com.sksamuel.elastic4s.mappings.MappingDefinition
import com.sksamuel.elastic4s.{IndexSettings, AnalysisDefinition, Executable, ProxyClients}
import org.elasticsearch.action.admin.indices.template.delete.{DeleteIndexTemplateAction, DeleteIndexTemplateRequest, DeleteIndexTemplateRequestBuilder, DeleteIndexTemplateResponse}
import org.elasticsearch.action.admin.indices.template.get.{GetIndexTemplatesAction, GetIndexTemplatesRequest, GetIndexTemplatesRequestBuilder, GetIndexTemplatesResponse}
import org.elasticsearch.action.admin.indices.template.put.{PutIndexTemplateAction, PutIndexTemplateRequest, PutIndexTemplateRequestBuilder, PutIndexTemplateResponse}
import org.elasticsearch.client.Client
import org.elasticsearch.common.xcontent.XContentFactory

import scala.collection.mutable.ListBuffer
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

case class CreateIndexTemplateDefinition(name: String, pattern: String) {
  require(name.nonEmpty, "template name must not be null or empty")
  require(pattern.nonEmpty, "pattern must not be null or empty")

  val _mappings = new ListBuffer[MappingDefinition]
  var _analysis: Option[AnalysisDefinition] = None
  val _settings = new IndexSettings

  val _builder = new PutIndexTemplateRequestBuilder(ProxyClients.indices, PutIndexTemplateAction.INSTANCE, name)
    .setTemplate(pattern)

  def mappings(mappings: MappingDefinition*): this.type = {
    _mappings appendAll mappings
    this
  }

  def analysis(analyzers: Iterable[AnalyzerDefinition]): this.type = {
    _analysis = Some(AnalysisDefinition(analyzers))
    this
  }

  def indexSetting(name: String, value: Any): this.type = {
    _settings.settings += name -> value
    this
  }
}

case class DeleteIndexTemplateDefinition(name: String) {
  def build: DeleteIndexTemplateRequest = _builder.request
  val _builder = new DeleteIndexTemplateRequestBuilder(ProxyClients.indices, DeleteIndexTemplateAction.INSTANCE, name)
}

case class GetTemplateDefinition(name: String) {
  def build: GetIndexTemplatesRequest = _builder.request
  val _builder = new GetIndexTemplatesRequestBuilder(ProxyClients.indices, GetIndexTemplatesAction.INSTANCE, name)
}
