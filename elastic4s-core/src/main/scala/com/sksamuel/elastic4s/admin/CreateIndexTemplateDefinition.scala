package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.ProxyClients
import com.sksamuel.elastic4s.analyzers.AnalyzerDefinition
import com.sksamuel.elastic4s.index.{AnalysisDefinition, IndexSettings}
import com.sksamuel.elastic4s.mappings.MappingDefinition
import org.elasticsearch.action.admin.indices.template.put.{PutIndexTemplateAction, PutIndexTemplateRequestBuilder}

import scala.collection.mutable.ListBuffer

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
