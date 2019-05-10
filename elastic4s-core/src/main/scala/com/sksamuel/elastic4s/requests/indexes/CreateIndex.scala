package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.requests.analyzers.{AnalyzerDefinition, NormalizerDefinition}
import com.sksamuel.elastic4s.requests.mappings.{Analysis, MappingDefinition}
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration._

case class IndexAliasRequest(name: String, filter: Option[Query] = None, routing: Option[String] = None)

case class CreateIndexRequest(name: String,
                              @deprecated("use the new analysis package", "7.0.1")
                              _analysis: Option[AnalysisDefinition] = None,
                              analysis: Option[com.sksamuel.elastic4s.requests.analysis.Analysis] = None,
                              mapping: Option[MappingDefinition] = None,
                              rawSource: Option[String] = None,
                              waitForActiveShards: Option[Int] = None,
                              aliases: Set[IndexAliasRequest] = Set.empty,
                              settings: IndexSettings = new IndexSettings,
                              includeTypeName: Option[Boolean] = None) {

  def alias(name: String): CreateIndexRequest = alias(IndexAliasRequest(name, None))
  def alias(name: String, filter: Query): CreateIndexRequest =
    alias(IndexAliasRequest(name, Option(filter)))
  def alias(definition: IndexAliasRequest): CreateIndexRequest = copy(aliases = aliases + definition)

  def singleShard(): CreateIndexRequest   = shards(1)
  def singleReplica(): CreateIndexRequest = replicas(1)

  def waitForActiveShards(shards: Int): CreateIndexRequest = copy(waitForActiveShards = shards.some)

  def shards(shds: Int): CreateIndexRequest    = copy(settings = settings.shards = shds)
  def replicas(repls: Int): CreateIndexRequest = copy(settings = settings.replicas = repls)

  def refreshInterval(duration: Duration): CreateIndexRequest = refreshInterval(duration.toMillis + "ms")
  def refreshInterval(interval: String): CreateIndexRequest   = copy(settings = settings.refreshInterval = interval)

  def settings(map: Map[String, Any]): CreateIndexRequest =
    copy(settings = map.foldLeft(new IndexSettings()) { case (setting, (key, value)) => setting.add(key, value) })

  def indexSetting(name: String, value: Any): CreateIndexRequest = copy(settings = settings.add(name, value))

  def mapping(mapping: MappingDefinition): CreateIndexRequest = copy(mapping = mapping.some)

  @deprecated("use mapping not mappings since creating an index only support a single mapping now", "7.0.0")
  def mappings(mapping: MappingDefinition): CreateIndexRequest = copy(mapping = mapping.some)

  @deprecated("use the new analysis package", "7.0.1")
  def analysis(first: AnalyzerDefinition, rest: AnalyzerDefinition*): CreateIndexRequest = analysis(first +: rest)

  @deprecated("use the new analysis package", "7.0.1")
  def analysis(analyzers: Iterable[AnalyzerDefinition]): CreateIndexRequest              = analysis(analyzers, Nil)

  def analysis(analysis: com.sksamuel.elastic4s.requests.analysis.Analysis): CreateIndexRequest = copy(analysis = analysis.some)

  @deprecated("use the new analysis package", "7.0.1")
  def analysis(analyzers: Iterable[AnalyzerDefinition],
               normalizers: Iterable[NormalizerDefinition]): CreateIndexRequest =
    _analysis match {
      case None => copy(_analysis = AnalysisDefinition(analyzers, normalizers).some)
      case Some(a) => copy(_analysis = AnalysisDefinition(a.analyzers ++ analyzers, a.normalizers ++ normalizers).some)
    }

  @deprecated("use the new analysis package", "7.0.1")
  def normalizers(first: NormalizerDefinition, rest: NormalizerDefinition*): CreateIndexRequest =
    analysis(Nil, first +: rest)

  @deprecated("use the new analysis package", "7.0.1")
  def normalizers(normalizers: Iterable[NormalizerDefinition]): CreateIndexRequest = analysis(Nil, normalizers)

  /**
    * Creates an index using the json provided as is.
    */
  def source(source: String): CreateIndexRequest = copy(rawSource = source.some)

  @deprecated("types are deprecated", "7.0.0")
  def includeTypeName(includeTypeName: Boolean): CreateIndexRequest = copy(includeTypeName = includeTypeName.some)

  @deprecated("types are deprecated", "7.0.0")
  def includeTypeName(includeTypeName: Option[Boolean]): CreateIndexRequest = copy(includeTypeName = includeTypeName)
}
