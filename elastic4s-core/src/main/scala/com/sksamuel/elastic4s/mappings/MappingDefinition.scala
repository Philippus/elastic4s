package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.IndexesAndType
import com.sksamuel.elastic4s.analyzers.Analyzer
import com.sksamuel.elastic4s.mappings.dynamictemplate.{DynamicMapping, DynamicTemplateDefinition}

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
  def routing: Option[RoutingDefinition]
  def timestamp: Option[TimestampDefinition]
  def templates: Seq[DynamicTemplateDefinition]
}

case class PutMappingDefinition(indexesAndType: IndexesAndType,
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
                                routing: Option[RoutingDefinition] = None,
                                timestamp: Option[TimestampDefinition] = None,
                                templates: Seq[DynamicTemplateDefinition] = Nil,
                                rawSource: Option[String] = None
                               ) extends MappingDefinitionLike {

  import com.sksamuel.exts.OptionImplicits._

  def all(all: Boolean): PutMappingDefinition = copy(all = all.some)
  def source(source: Boolean): PutMappingDefinition = copy(source = source.some)
  def rawSource(rawSource: String): PutMappingDefinition = copy(rawSource = rawSource.some)

  def sourceExcludes(sourceExcludes: String*): PutMappingDefinition = copy(sourceExcludes = sourceExcludes)
  def sourceExcludes(sourceExcludes: Iterable[String]): PutMappingDefinition =
    copy(sourceExcludes = sourceExcludes.toSeq)

  def analyzer(analyzer: String): PutMappingDefinition = copy(analyzer = analyzer.some)
  def analyzer(analyzer: Analyzer): PutMappingDefinition = copy(analyzer = analyzer.name.some)

  @deprecated("use boostName", "5.2")
  def boost(boostName: String): PutMappingDefinition = copy(boostName = boostName.some)
  def boostName(boostName: String): PutMappingDefinition = copy(boostName = boostName.some)

  def boostNullValue(boostNullValue: Double): PutMappingDefinition = copy(boostNullValue = boostNullValue.some)
  def parent(parent: String): PutMappingDefinition = copy(parent = parent.some)
  def dynamic(dynamic: DynamicMapping): PutMappingDefinition = copy(dynamic = dynamic.some)
  def meta(map: Map[String, Any]): PutMappingDefinition = copy(meta = map)
  def dateDetection(dateDetection: Boolean): PutMappingDefinition = copy(dateDetection = dateDetection.some)
  def numericDetection(numericDetection: Boolean): PutMappingDefinition = copy(numericDetection = numericDetection.some)

  def fields(fields: Iterable[FieldDefinition]): PutMappingDefinition = as(fields)
  def fields(fields: FieldDefinition*): PutMappingDefinition = as(fields: _*)

  def as(fields: FieldDefinition*): PutMappingDefinition = as(fields.toIterable)
  def as(iterable: Iterable[FieldDefinition]): PutMappingDefinition = copy(fields = fields ++ iterable)

  def timestamp(enabled: Boolean,
                path: Option[String] = None,
                format: Option[String] = None,
                default: Option[String] = None): PutMappingDefinition =
    copy(timestamp = TimestampDefinition(enabled, path, format, default).some)

  def timestamp(t: TimestampDefinition): PutMappingDefinition = copy(timestamp = t.some)

  def dynamicDateFormats(dynamic_date_formats: String*): PutMappingDefinition =
    copy(dynamicDateFormats = dynamic_date_formats.toSeq)

  def dynamicDateFormats(dynamic_date_formats: Iterable[String]): PutMappingDefinition =
    copy(dynamicDateFormats = dynamic_date_formats.toSeq)

  def routing(required: Boolean, path: Option[String] = None): PutMappingDefinition =
    copy(routing = Some(RoutingDefinition(required, path)))

  def size(size: Boolean): PutMappingDefinition = copy(size = size.some)

  def dynamicTemplates(temps: Iterable[DynamicTemplateDefinition]): PutMappingDefinition = templates(temps)
  def dynamicTemplates(temps: DynamicTemplateDefinition*): PutMappingDefinition = templates(temps)
  def templates(temps: Iterable[DynamicTemplateDefinition]): PutMappingDefinition = copy(templates = temps.toSeq)
  def templates(temps: DynamicTemplateDefinition*): PutMappingDefinition = copy(templates = temps.toSeq)
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
                             routing: Option[RoutingDefinition] = None,
                             timestamp: Option[TimestampDefinition] = None,
                             templates: Seq[DynamicTemplateDefinition] = Nil,
                             rawSource: Option[String] = None
                            ) extends MappingDefinitionLike {

  import com.sksamuel.exts.OptionImplicits._

  def all(all: Boolean): MappingDefinition = copy(all = all.some)
  def source(source: Boolean): MappingDefinition = copy(source = source.some)
  def sourceExcludes(sourceExcludes: String*): MappingDefinition = copy(sourceExcludes = sourceExcludes)
  def sourceExcludes(sourceExcludes: Iterable[String]): MappingDefinition = copy(sourceExcludes = sourceExcludes.toSeq)
  def analyzer(analyzer: String): MappingDefinition = copy(analyzer = analyzer.some)
  def analyzer(analyzer: Analyzer): MappingDefinition = copy(analyzer = analyzer.name.some)

  @deprecated("use boostName", "5.2")
  def boost(boostName: String): MappingDefinition = copy(boostName = boostName.some)

  def boostName(boostName: String): MappingDefinition = copy(boostName = boostName.some)
  def boostNullValue(boostNullValue: Double): MappingDefinition = copy(boostNullValue = boostNullValue.some)

  def parent(parent: String): MappingDefinition = copy(parent = parent.some)

  def dynamic(dynamic: DynamicMapping): MappingDefinition = copy(dynamic = dynamic.some)
  def meta(map: Map[String, Any]): MappingDefinition = copy(meta = map)
  def dateDetection(dateDetection: Boolean): MappingDefinition = copy(dateDetection = dateDetection.some)
  def numericDetection(numericDetection: Boolean): MappingDefinition = copy(numericDetection = numericDetection.some)

  def fields(fields: Iterable[FieldDefinition]): MappingDefinition = as(fields)
  def fields(fields: FieldDefinition*): MappingDefinition = as(fields)

  def as(fields: FieldDefinition*): MappingDefinition = as(fields.toIterable)
  def as(iterable: Iterable[FieldDefinition]): MappingDefinition = copy(fields = fields ++ iterable)

  def timestamp(enabled: Boolean,
                path: Option[String] = None,
                format: Option[String] = None,
                default: Option[String] = None): MappingDefinition =
    copy(timestamp = TimestampDefinition(enabled, path, format, default).some)

  def timestamp(t: TimestampDefinition): MappingDefinition = copy(timestamp = t.some)

  def dynamicDateFormats(dynamic_date_formats: String*): MappingDefinition =
    copy(dynamicDateFormats = dynamic_date_formats.toSeq)

  def dynamicDateFormats(dynamic_date_formats: Iterable[String]): MappingDefinition =
    copy(dynamicDateFormats = dynamic_date_formats.toSeq)

  def routing(required: Boolean, path: Option[String] = None): MappingDefinition =
    copy(routing = Some(RoutingDefinition(required, path)))

  def size(size: Boolean): MappingDefinition = copy(size = size.some)

  def dynamicTemplates(temps: Iterable[DynamicTemplateDefinition]): MappingDefinition = templates(temps)
  def dynamicTemplates(temps: DynamicTemplateDefinition*): MappingDefinition = templates(temps)
  def templates(temps: Iterable[DynamicTemplateDefinition]): MappingDefinition = copy(templates = temps.toSeq)
  def templates(temps: DynamicTemplateDefinition*): MappingDefinition = copy(templates = temps.toSeq)
}
