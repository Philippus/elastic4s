package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.requests.analyzers.{AnalyzerDefinition, NormalizerDefinition}
import com.sksamuel.elastic4s.requests.mappings.MappingDefinition
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration._

case class IndexAliasRequest(name: String, filter: Option[Query] = None, routing: Option[String] = None)

case class CreateIndexRequest(name: String,
                              analysis: Option[AnalysisDefinition] = None,
                              mappings: Seq[MappingDefinition] = Nil,
                              rawSource: Option[String] = None,
                              waitForActiveShards: Option[Int] = None,
                              aliases: Set[IndexAliasRequest] = Set.empty,
                              settings: IndexSettings = new IndexSettings) {

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

  def mappings(first: MappingDefinition, rest: MappingDefinition*): CreateIndexRequest = mappings(first +: rest)
  def mappings(mappings: Iterable[MappingDefinition]): CreateIndexRequest =
    copy(mappings = this.mappings ++ mappings)

  def analysis(first: AnalyzerDefinition, rest: AnalyzerDefinition*): CreateIndexRequest = analysis(first +: rest)
  def analysis(analyzers: Iterable[AnalyzerDefinition]): CreateIndexRequest              = analysis(analyzers, Nil)
  def analysis(analyzers: Iterable[AnalyzerDefinition],
               normalizers: Iterable[NormalizerDefinition]): CreateIndexRequest =
    analysis match {
      case None    => copy(analysis = AnalysisDefinition(analyzers, normalizers).some)
      case Some(a) => copy(analysis = AnalysisDefinition(a.analyzers ++ analyzers, a.normalizers ++ normalizers).some)
    }

  def normalizers(first: NormalizerDefinition, rest: NormalizerDefinition*): CreateIndexRequest =
    analysis(Nil, first +: rest)
  def normalizers(normalizers: Iterable[NormalizerDefinition]): CreateIndexRequest = analysis(Nil, normalizers)

  def source(source: String): CreateIndexRequest = copy(rawSource = source.some)
}
