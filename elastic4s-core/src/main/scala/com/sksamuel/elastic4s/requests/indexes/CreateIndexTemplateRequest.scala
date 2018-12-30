package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.requests.analyzers.{AnalyzerDefinition, NormalizerDefinition}
import com.sksamuel.elastic4s.requests.mappings.MappingDefinition
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class TemplateAlias(name: String, filter: Option[Query] = None, routing: Option[String] = None) {
  def filter(filter: Query): TemplateAlias    = copy(filter = filter.some)
  def routing(routing: String): TemplateAlias = copy(routing = routing.some)
}

case class IndexTemplateExistsRequest()

case class CreateIndexTemplateRequest(name: String,
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

  def analysis(first: AnalyzerDefinition, rest: AnalyzerDefinition*): CreateIndexTemplateRequest =
    analysis(first +: rest, Nil)

  def analysis(analyzers: Iterable[AnalyzerDefinition]): CreateIndexTemplateRequest =
    analysis(analyzers, Nil)

  def analysis(analyzers: Iterable[AnalyzerDefinition],
               normalizers: Iterable[NormalizerDefinition]): CreateIndexTemplateRequest =
    analysis match {
      case None    => copy(analysis = AnalysisDefinition(analyzers, normalizers).some)
      case Some(a) => copy(analysis = AnalysisDefinition(a.analyzers ++ analyzers, a.normalizers ++ normalizers).some)
    }

  def normalizers(first: NormalizerDefinition, rest: NormalizerDefinition*): CreateIndexTemplateRequest =
    analysis(Nil, first +: rest)
  def normalizers(normalizers: Iterable[NormalizerDefinition]): CreateIndexTemplateRequest =
    analysis(Nil, normalizers)

  def mappings(first: MappingDefinition, rest: MappingDefinition*): CreateIndexTemplateRequest =
    mappings(first +: rest)

  def mappings(mappings: Iterable[MappingDefinition]): CreateIndexTemplateRequest = copy(mappings = mappings.toSeq)

  @deprecated("use settings(map)", "6.0.0")
  def indexSetting(key: String, value: Double): CreateIndexTemplateRequest = settings(Map(key -> value))

  @deprecated("use settings(map)", "6.0.0")
  def indexSetting(key: String, value: Long): CreateIndexTemplateRequest = settings(Map(key -> value))

  @deprecated("use settings(map)", "6.0.0")
  def indexSetting(key: String, value: Boolean): CreateIndexTemplateRequest = settings(Map(key -> value))

  @deprecated("use settings(map)", "6.0.0")
  def indexSetting(key: String, value: String): CreateIndexTemplateRequest = settings(Map(key -> value))

  def version(version: Int): CreateIndexTemplateRequest = copy(version = version.some)

  // replaces all settings with the given settings
  def settings(settings: Map[String, Any]): CreateIndexTemplateRequest = copy(settings = settings)

  def order(order: Int): CreateIndexTemplateRequest       = copy(order = order.some)
  def create(create: Boolean): CreateIndexTemplateRequest = copy(create = create.some)

  def aliases(first: TemplateAlias, rest: TemplateAlias*): CreateIndexTemplateRequest = aliases(first +: rest)
  def aliases(aliases: Iterable[TemplateAlias]): CreateIndexTemplateRequest           = copy(aliases = aliases.toSeq)
}
