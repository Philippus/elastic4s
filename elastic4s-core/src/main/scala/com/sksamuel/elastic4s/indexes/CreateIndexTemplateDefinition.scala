package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.analyzers.{AnalyzerDefinition, NormalizerDefinition}
import com.sksamuel.elastic4s.mappings.MappingDefinition
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

case class TemplateAlias(name: String,
                         filter: Option[QueryDefinition] = None,
                         routing: Option[String] = None) {
  def filter(filter: QueryDefinition): TemplateAlias = copy(filter = filter.some)
  def routing(routing: String): TemplateAlias = copy(routing = routing.some)
}

case class CreateIndexTemplateDefinition(name: String,
                                         pattern: String,
                                         settings: Map[String, Any] = Map.empty,
                                         mappings: Seq[MappingDefinition] = Nil,
                                         analysis: Option[AnalysisDefinition] = None,
                                         order: Option[Int] = None,
                                         version: Option[Int] = None,
                                         create: Option[Boolean] = None,
                                         aliases: Seq[TemplateAlias] = Nil) {
  require(name.nonEmpty, "template name must not be null or empty")
  require(pattern.nonEmpty, "pattern must not be null or empty")

  def analysis(first: AnalyzerDefinition, rest: AnalyzerDefinition*): CreateIndexTemplateDefinition =
    analysis(first +: rest, Nil)

  def analysis(analyzers: Iterable[AnalyzerDefinition]): CreateIndexTemplateDefinition =
    analysis(analyzers, Nil)

  def analysis(analyzers: Iterable[AnalyzerDefinition], normalizers: Iterable[NormalizerDefinition]): CreateIndexTemplateDefinition =
    analysis match {
      case None => copy(analysis = AnalysisDefinition(analyzers, normalizers).some)
      case Some(a) => copy(analysis = AnalysisDefinition(a.analyzers ++ analyzers, a.normalizers ++ normalizers).some)
    }

  def normalizers(first: NormalizerDefinition,
                  rest: NormalizerDefinition*): CreateIndexTemplateDefinition = analysis(Nil, first +: rest)
  def normalizers(normalizers: Iterable[NormalizerDefinition]): CreateIndexTemplateDefinition = analysis(Nil, normalizers)

  def mappings(first: MappingDefinition,
               rest: MappingDefinition*): CreateIndexTemplateDefinition = mappings(first +: rest)

  def mappings(mappings: Iterable[MappingDefinition]): CreateIndexTemplateDefinition = copy(mappings = mappings.toSeq)

  @deprecated("use settings(map)", "6.0.0")
  def indexSetting(key: String, value: Double): CreateIndexTemplateDefinition = settings(Map(key -> value))

  @deprecated("use settings(map)", "6.0.0")
  def indexSetting(key: String, value: Long): CreateIndexTemplateDefinition = settings(Map(key -> value))

  @deprecated("use settings(map)", "6.0.0")
  def indexSetting(key: String, value: Boolean): CreateIndexTemplateDefinition = settings(Map(key -> value))

  @deprecated("use settings(map)", "6.0.0")
  def indexSetting(key: String, value: String): CreateIndexTemplateDefinition = settings(Map(key -> value))

  def version(version: Int): CreateIndexTemplateDefinition = copy(version = version.some)

  // replaces all settings with the given settings
  def settings(settings: Map[String, Any]): CreateIndexTemplateDefinition = copy(settings = settings)

  def order(order: Int): CreateIndexTemplateDefinition = copy(order = order.some)
  def create(create: Boolean): CreateIndexTemplateDefinition = copy(create = create.some)

  def aliases(first: TemplateAlias, rest: TemplateAlias*): CreateIndexTemplateDefinition = aliases(first +: rest)
  def aliases(aliases: Iterable[TemplateAlias]): CreateIndexTemplateDefinition = copy(aliases = aliases.toSeq)
}
