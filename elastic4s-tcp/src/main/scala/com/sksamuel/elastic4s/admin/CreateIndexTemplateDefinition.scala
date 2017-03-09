package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.analyzers.{AnalyzerDefinition, NormalizerDefinition}
import com.sksamuel.elastic4s.indexes.{AnalysisContentBuilder, AnalysisDefinition}
import com.sksamuel.elastic4s.mappings.{MappingContentBuilder, MappingDefinition}
import org.elasticsearch.action.admin.indices.alias.Alias
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequestBuilder
import org.elasticsearch.common.settings.Settings
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.common.io.stream.BytesStreamOutput
import org.elasticsearch.common.xcontent.XContentFactory

import scala.collection.JavaConverters._

case class CreateIndexTemplateDefinition(name: String,
                                         pattern: String,
                                         settings: Settings = Settings.EMPTY,
                                         mappings: Seq[MappingDefinition] = Nil,
                                         analysis: Option[AnalysisDefinition] = None,
                                         order: Option[Int] = None,
                                         create: Option[Boolean] = None,
                                         aliases: Seq[Alias] = Nil) {

  require(name.nonEmpty, "template name must not be null or empty")
  require(pattern.nonEmpty, "pattern must not be null or empty")

  def populate(builder: PutIndexTemplateRequestBuilder) = {

    builder.setTemplate(pattern)
    order.foreach(builder.setOrder)
    create.foreach(builder.setCreate)
    aliases.foreach(builder.addAlias)

    mappings.foreach { mapping =>
      builder.addMapping(mapping.`type`, MappingContentBuilder.buildWithName(mapping, mapping.`type`))
    }

    if (!settings.isEmpty || analysis.nonEmpty) {
      val source = XContentFactory.jsonBuilder().startObject()
      settings.getAsMap.asScala.foreach { p => source.field(p._1, p._2) }
      analysis.foreach(AnalysisContentBuilder.build(_, source))
      source.endObject()
      builder.setSettings(source.string())
    }

    val output = new BytesStreamOutput()
    builder.request().writeTo(output)
    builder
  }

  def analysis(first: AnalyzerDefinition, rest: AnalyzerDefinition*): CreateIndexTemplateDefinition = analysis(first +: rest, Nil)

  def analysis(analyzers: Iterable[AnalyzerDefinition]): CreateIndexTemplateDefinition = analysis(analyzers, Nil)

  def analysis(analyzers: Iterable[AnalyzerDefinition], normalizers: Iterable[NormalizerDefinition]): CreateIndexTemplateDefinition =
    analysis match {
      case None    => copy(analysis = AnalysisDefinition(analyzers, normalizers).some)
      case Some(a) => copy(analysis = AnalysisDefinition(a.analyzers ++ analyzers, a.normalizers ++ normalizers).some)
    }

  def normalizers(first: NormalizerDefinition, rest: NormalizerDefinition*): CreateIndexTemplateDefinition = analysis(Nil, first +: rest)
  def normalizers(normalizers: Iterable[NormalizerDefinition]): CreateIndexTemplateDefinition = analysis(Nil, normalizers)

  def mappings(first: MappingDefinition, rest: MappingDefinition*): CreateIndexTemplateDefinition =
    mappings(first +: rest)

  def mappings(mappings: Iterable[MappingDefinition]): CreateIndexTemplateDefinition = copy(mappings = mappings.toSeq)

  // adds a new setting
  def indexSetting(key: String, value: Double) = settings(Settings.builder().put(settings).put(key, value).build())
  def indexSetting(key: String, value: Long) = settings(Settings.builder().put(settings).put(key, value).build())
  def indexSetting(key: String, value: Boolean) = settings(Settings.builder().put(settings).put(key, value).build())
  def indexSetting(key: String, value: String) = settings(Settings.builder().put(settings).put(key, value).build())

  // replaces all settings with the given settings
  def settings(settings: Settings): CreateIndexTemplateDefinition = copy(settings = settings)
  def order(order: Int): CreateIndexTemplateDefinition = copy(order = order.some)
  def create(create: Boolean): CreateIndexTemplateDefinition = copy(create = create.some)
  def alias(alias: Alias): CreateIndexTemplateDefinition = aliases(Seq(alias))
  def aliases(aliases: Seq[Alias]): CreateIndexTemplateDefinition = copy(aliases = aliases)
}
