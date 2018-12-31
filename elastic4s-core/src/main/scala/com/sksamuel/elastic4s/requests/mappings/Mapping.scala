package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.IndexesAndType
import com.sksamuel.elastic4s.requests.analyzers.Analyzer
import com.sksamuel.elastic4s.requests.mappings.dynamictemplate.{DynamicMapping, DynamicTemplateRequest}

trait MappingDefinitionLike {
  def all: Option[Boolean]
  def source: Option[Boolean]
  def sourceExcludes: Seq[String]
  def dateDetection: Option[Boolean]
  def numericDetection: Option[Boolean]
  def size: Option[Boolean]
  def dynamicDateFormats: Seq[String]
  def fields: Seq[FieldDefinition]
  def analyzer: Option[String]
  def boostName: Option[String]
  def boostNullValue: Option[Double]
  def parent: Option[String]
  def dynamic: Option[DynamicMapping]
  def meta: Map[String, Any]
  def routing: Option[Routing]
  def templates: Seq[DynamicTemplateRequest]
  def rawSource: Option[String]
}

case class PutMappingRequest(indexesAndType: IndexesAndType,
                             updateAllTypes: Option[Boolean] = None,
                             ignoreUnavailable: Option[Boolean] = None,
                             allowNoIndices: Option[Boolean] = None,
                             expandWildcards: Option[Boolean] = None,
                             all: Option[Boolean] = None,
                             source: Option[Boolean] = None,
                             sourceExcludes: Seq[String] = Nil,
                             dateDetection: Option[Boolean] = None,
                             numericDetection: Option[Boolean] = None,
                             size: Option[Boolean] = None,
                             dynamicDateFormats: Seq[String] = Nil,
                             fields: Seq[FieldDefinition] = Nil,
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

  def all(all: Boolean): PutMappingRequest       = copy(all = all.some)
  def source(source: Boolean): PutMappingRequest = copy(source = source.some)

  // the raw source should include proeprties but not the type
  def rawSource(rawSource: String): PutMappingRequest = copy(rawSource = rawSource.some)

  def sourceExcludes(sourceExcludes: String*): PutMappingRequest = copy(sourceExcludes = sourceExcludes)
  def sourceExcludes(sourceExcludes: Iterable[String]): PutMappingRequest =
    copy(sourceExcludes = sourceExcludes.toSeq)

  def analyzer(analyzer: String): PutMappingRequest   = copy(analyzer = analyzer.some)
  def analyzer(analyzer: Analyzer): PutMappingRequest = copy(analyzer = analyzer.name.some)

  @deprecated("use boostName", "5.2")
  def boost(boostName: String): PutMappingRequest     = copy(boostName = boostName.some)
  def boostName(boostName: String): PutMappingRequest = copy(boostName = boostName.some)

  def boostNullValue(boostNullValue: Double): PutMappingRequest      = copy(boostNullValue = boostNullValue.some)
  def parent(parent: String): PutMappingRequest                      = copy(parent = parent.some)
  def dynamic(dynamic: DynamicMapping): PutMappingRequest            = copy(dynamic = dynamic.some)
  def meta(map: Map[String, Any]): PutMappingRequest                 = copy(meta = map)
  def dateDetection(dateDetection: Boolean): PutMappingRequest       = copy(dateDetection = dateDetection.some)
  def numericDetection(numericDetection: Boolean): PutMappingRequest = copy(numericDetection = numericDetection.some)

  def fields(fields: Iterable[FieldDefinition]): PutMappingRequest = as(fields)
  def fields(fields: FieldDefinition*): PutMappingRequest          = as(fields: _*)

  def as(fields: FieldDefinition*): PutMappingRequest            = as(fields.toIterable)
  def as(iterable: Iterable[FieldDefinition]): PutMappingRequest = copy(fields = fields ++ iterable)

  def dynamicDateFormats(dynamic_date_formats: String*): PutMappingRequest =
    copy(dynamicDateFormats = dynamic_date_formats.toSeq)

  def dynamicDateFormats(dynamic_date_formats: Iterable[String]): PutMappingRequest =
    copy(dynamicDateFormats = dynamic_date_formats.toSeq)

  def routing(required: Boolean, path: Option[String] = None): PutMappingRequest =
    copy(routing = Some(Routing(required, path)))

  def size(size: Boolean): PutMappingRequest = copy(size = size.some)

  def dynamicTemplates(temps: Iterable[DynamicTemplateRequest]): PutMappingRequest = templates(temps)
  def dynamicTemplates(temps: DynamicTemplateRequest*): PutMappingRequest          = templates(temps)
  def templates(temps: Iterable[DynamicTemplateRequest]): PutMappingRequest        = copy(templates = temps.toSeq)
  def templates(temps: DynamicTemplateRequest*): PutMappingRequest                 = copy(templates = temps.toSeq)
}

case class MappingDefinition(`type`: String, // the name basically, called a type in es
                             all: Option[Boolean] = None,
                             source: Option[Boolean] = None,
                             sourceExcludes: Seq[String] = Nil,
                             dateDetection: Option[Boolean] = None,
                             numericDetection: Option[Boolean] = None,
                             size: Option[Boolean] = None,
                             dynamicDateFormats: Seq[String] = Nil,
                             fields: Seq[FieldDefinition] = Nil,
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

  def all(all: Boolean): MappingDefinition                                = copy(all = all.some)
  def source(source: Boolean): MappingDefinition                          = copy(source = source.some)
  def sourceExcludes(sourceExcludes: String*): MappingDefinition          = copy(sourceExcludes = sourceExcludes)
  def sourceExcludes(sourceExcludes: Iterable[String]): MappingDefinition = copy(sourceExcludes = sourceExcludes.toSeq)
  def analyzer(analyzer: String): MappingDefinition                       = copy(analyzer = analyzer.some)
  def analyzer(analyzer: Analyzer): MappingDefinition                     = copy(analyzer = analyzer.name.some)

  @deprecated("use boostName", "5.2")
  def boost(boostName: String): MappingDefinition = copy(boostName = boostName.some)

  def boostName(boostName: String): MappingDefinition           = copy(boostName = boostName.some)
  def boostNullValue(boostNullValue: Double): MappingDefinition = copy(boostNullValue = boostNullValue.some)

  def parent(parent: String): MappingDefinition = copy(parent = parent.some)

  def dynamic(dynamic: DynamicMapping): MappingDefinition            = copy(dynamic = dynamic.some)
  def meta(map: Map[String, Any]): MappingDefinition                 = copy(meta = map)
  def dateDetection(dateDetection: Boolean): MappingDefinition       = copy(dateDetection = dateDetection.some)
  def numericDetection(numericDetection: Boolean): MappingDefinition = copy(numericDetection = numericDetection.some)

  def fields(fields: Iterable[FieldDefinition]): MappingDefinition = as(fields)
  def fields(fields: FieldDefinition*): MappingDefinition          = as(fields)

  def as(fields: FieldDefinition*): MappingDefinition            = as(fields.toIterable)
  def as(iterable: Iterable[FieldDefinition]): MappingDefinition = copy(fields = fields ++ iterable)

  def dynamicDateFormats(dynamic_date_formats: String*): MappingDefinition =
    copy(dynamicDateFormats = dynamic_date_formats.toSeq)

  def dynamicDateFormats(dynamic_date_formats: Iterable[String]): MappingDefinition =
    copy(dynamicDateFormats = dynamic_date_formats.toSeq)

  def routing(required: Boolean, path: Option[String] = None): MappingDefinition =
    copy(routing = Some(Routing(required, path)))

  def size(size: Boolean): MappingDefinition = copy(size = size.some)

  def rawSource(source: String): MappingDefinition = copy(rawSource = source.some)

  def dynamicTemplates(temps: Iterable[DynamicTemplateRequest]): MappingDefinition = templates(temps)
  def dynamicTemplates(temps: DynamicTemplateRequest*): MappingDefinition          = templates(temps)
  def templates(temps: Iterable[DynamicTemplateRequest]): MappingDefinition        = copy(templates = temps.toSeq)
  def templates(temps: DynamicTemplateRequest*): MappingDefinition                 = copy(templates = temps.toSeq)
}
