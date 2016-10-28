package com.sksamuel.elastic4s2.index

import com.sksamuel.elastic4s2.analyzers.AnalyzerDefinition
import com.sksamuel.elastic4s2.mappings.MappingDefinition
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}
import scala.concurrent.duration._

case class CreateIndexDefinition(name: String) {
  require(!name.contains("/"), "Index should not contain / when creating mappings. Specify the type as the mapping")

  val _mappings = new scala.collection.mutable.ListBuffer[MappingDefinition]
  val _settings = new IndexSettings
  var _analysis: Option[AnalysisDefinition] = None

  def build = _rawSource match {
    case Some(s) => new CreateIndexRequest(name).source(s)
    case None => new CreateIndexRequest(name).source(_source)
  }

  def shards(shards: Int): CreateIndexDefinition = {
    _settings.shards = shards
    this
  }

  def replicas(replicas: Int): CreateIndexDefinition = {
    _settings.replicas = replicas
    this
  }

  def refreshInterval(duration: Duration): this.type = refreshInterval(duration.toMillis + "ms")

  def refreshInterval(interval: String): this.type = {
    _settings.refreshInterval = interval
    this
  }

  def indexSetting(name: String, value: Any): CreateIndexDefinition = {
    _settings.settings += name -> value
    this
  }

  def mappings(mappings: MappingDefinition*): CreateIndexDefinition = {
    _mappings ++= mappings
    this
  }

  def mappings(mappings: Iterable[MappingDefinition]): CreateIndexDefinition = {
    _mappings ++= mappings
    this
  }

  def analysis(analyzers: Iterable[AnalyzerDefinition]): this.type = {
    _analysis = Some(AnalysisDefinition(analyzers))
    this
  }

  def analysis(first: AnalyzerDefinition, rest: AnalyzerDefinition*): this.type = analysis(first +: rest)

  var _rawSource: Option[String] = None

  def source(source: String): CreateIndexDefinition = {
    _rawSource = Some(source)
    this
  }

  private[elastic4s2] def _source: XContentBuilder = {
    val source = XContentFactory.jsonBuilder().startObject()

    if (_settings.settings.nonEmpty || _analysis.nonEmpty) {
      source.startObject("settings")

      if (_settings.settings.nonEmpty) {
        source.startObject("index")

        _settings.settings foreach {
          case (key, value) =>
            source.field(key, value)
        }

        source.endObject()
      }

      _analysis.foreach(_.build(source))

      source.endObject() // end settings
    }

    if (_mappings.nonEmpty) {
      source.startObject("mappings")
      for (mapping <- _mappings) {
        source.startObject(mapping.`type`)
        mapping.build(source)
        source.endObject()
      }
      source.endObject()
    }

    source.endObject()
  }
}
