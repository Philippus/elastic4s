package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.fields.ElasticField
import com.sksamuel.elastic4s.requests.analyzers.Analyzer
import com.sksamuel.elastic4s.requests.mappings.dynamictemplate.{DynamicMapping, DynamicTemplateRequest}

case class MappingDefinition(properties: Seq[ElasticField] = Nil,
                             all: Option[Boolean] = None,
                             source: Option[Boolean] = None,
                             sourceExcludes: Seq[String] = Nil,
                             dateDetection: Option[Boolean] = None,
                             numericDetection: Option[Boolean] = None,
                             size: Option[Boolean] = None,
                             dynamicDateFormats: Seq[String] = Nil,
                             analyzer: Option[String] = None,
                             boostName: Option[String] = None,
                             boostNullValue: Option[Double] = None,
                             parent: Option[String] = None,
                             dynamic: Option[DynamicMapping] = None,
                             meta: Map[String, Any] = Map.empty,
                             routing: Option[Routing] = None,
                             templates: Seq[DynamicTemplateRequest] = Nil,
                             rawSource: Option[String] = None)
  extends MappingDefinitionLike {

  import com.sksamuel.exts.OptionImplicits._

  def all(all: Boolean): MappingDefinition = copy(all = all.some)
  def source(source: Boolean): MappingDefinition = copy(source = source.some)
  def sourceExcludes(sourceExcludes: String*): MappingDefinition = copy(sourceExcludes = sourceExcludes)
  def sourceExcludes(sourceExcludes: Iterable[String]): MappingDefinition = copy(sourceExcludes = sourceExcludes.toSeq)
  def analyzer(analyzer: String): MappingDefinition = copy(analyzer = analyzer.some)

  @deprecated("use new analysis package", "7.2.0")
  def analyzer(analyzer: Analyzer): MappingDefinition = copy(analyzer = analyzer.name.some)

  def boostName(boostName: String): MappingDefinition = copy(boostName = boostName.some)
  def boostNullValue(boostNullValue: Double): MappingDefinition = copy(boostNullValue = boostNullValue.some)

  def parent(parent: String): MappingDefinition = copy(parent = parent.some)

  def dynamic(dynamic: DynamicMapping): MappingDefinition = copy(dynamic = dynamic.some)
  def dateDetection(dateDetection: Boolean): MappingDefinition = copy(dateDetection = dateDetection.some)
  def numericDetection(numericDetection: Boolean): MappingDefinition = copy(numericDetection = numericDetection.some)

  def meta(map: Map[String, Any]): MappingDefinition = copy(meta = map)

  def properties(fields: Iterable[ElasticField]): MappingDefinition = as(fields)
  def properties(fields: ElasticField*): MappingDefinition = as(fields)

  def as(fields: ElasticField*): MappingDefinition = as(fields.toIterable)
  def as(iterable: Iterable[ElasticField]): MappingDefinition = copy(properties = properties ++ iterable)

  def dynamicDateFormats(dynamic_date_formats: String*): MappingDefinition =
    copy(dynamicDateFormats = dynamic_date_formats.toSeq)

  def dynamicDateFormats(dynamic_date_formats: Iterable[String]): MappingDefinition =
    copy(dynamicDateFormats = dynamic_date_formats.toSeq)

  def routing(required: Boolean, path: Option[String] = None): MappingDefinition =
    copy(routing = Some(Routing(required, path)))

  def size(size: Boolean): MappingDefinition = copy(size = size.some)

  def rawSource(source: String): MappingDefinition = copy(rawSource = source.some)

  def dynamicTemplates(temps: Iterable[DynamicTemplateRequest]): MappingDefinition = templates(temps)
  def dynamicTemplates(temps: DynamicTemplateRequest*): MappingDefinition = templates(temps)
  def templates(temps: Iterable[DynamicTemplateRequest]): MappingDefinition = copy(templates = temps.toSeq)
  def templates(temps: DynamicTemplateRequest*): MappingDefinition = copy(templates = temps.toSeq)
}
