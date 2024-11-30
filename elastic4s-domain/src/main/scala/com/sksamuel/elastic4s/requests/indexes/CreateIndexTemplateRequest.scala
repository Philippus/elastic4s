package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.ext.OptionImplicits._
import com.sksamuel.elastic4s.requests.analyzers.{AnalyzerDefinition, NormalizerDefinition}
import com.sksamuel.elastic4s.requests.mappings.MappingDefinition

case class CreateIndexTemplateRequest(
    name: String,
    pattern: Seq[String],
    settings: Map[String, Any] = Map.empty,
    mappings: Seq[MappingDefinition] = Nil,
    @deprecated("use the new analysis package", "7.0.1")
    _analysis: Option[AnalysisDefinition] = None,
    analysis: Option[com.sksamuel.elastic4s.analysis.Analysis] = None,
    version: Option[Int] = None,
    create: Option[Boolean] = None,
    priority: Option[Int] = None,
    aliases: Seq[TemplateAlias] = Nil
) {
  require(name.nonEmpty, "template name must not be null or empty")
  require(pattern.nonEmpty, "pattern list must not be null or empty")
  require(!pattern.exists(_.isEmpty), "patterns must not be null or empty")

  @deprecated("use new analysis package", "7.2.0")
  def analysis(first: AnalyzerDefinition, rest: AnalyzerDefinition*): CreateIndexTemplateRequest =
    analysis(first +: rest, Nil)

  @deprecated("use new analysis package", "7.2.0")
  def analysis(analyzers: Iterable[AnalyzerDefinition]): CreateIndexTemplateRequest =
    analysis(analyzers, Nil)

  def analysis(analysis: com.sksamuel.elastic4s.analysis.Analysis): CreateIndexTemplateRequest =
    copy(analysis = analysis.some)

  @deprecated("use new analysis package", "7.2.0")
  def analysis(
      analyzers: Iterable[AnalyzerDefinition],
      normalizers: Iterable[NormalizerDefinition]
  ): CreateIndexTemplateRequest =
    _analysis match {
      case None    => copy(_analysis = AnalysisDefinition(analyzers, normalizers).some)
      case Some(a) => copy(_analysis = AnalysisDefinition(a.analyzers ++ analyzers, a.normalizers ++ normalizers).some)
    }

  @deprecated("use new analysis package", "7.2.0")
  def normalizers(first: NormalizerDefinition, rest: NormalizerDefinition*): CreateIndexTemplateRequest =
    analysis(Nil, first +: rest)

  @deprecated("use new analysis package", "7.2.0")
  def normalizers(normalizers: Iterable[NormalizerDefinition]): CreateIndexTemplateRequest =
    analysis(Nil, normalizers)

  def mappings(first: MappingDefinition, rest: MappingDefinition*): CreateIndexTemplateRequest =
    mappings(first +: rest)

  def mappings(mappings: Iterable[MappingDefinition]): CreateIndexTemplateRequest = copy(mappings = mappings.toSeq)

  def version(version: Int): CreateIndexTemplateRequest   = copy(version = version.some)
  def priority(priority: Int): CreateIndexTemplateRequest = copy(priority = priority.some)

  // replaces all settings with the given settings
  def settings(settings: Map[String, Any]): CreateIndexTemplateRequest = copy(settings = settings)

  def create(create: Boolean): CreateIndexTemplateRequest = copy(create = create.some)

  def aliases(first: TemplateAlias, rest: TemplateAlias*): CreateIndexTemplateRequest = aliases(first +: rest)
  def aliases(aliases: Iterable[TemplateAlias]): CreateIndexTemplateRequest           = copy(aliases = aliases.toSeq)
}
