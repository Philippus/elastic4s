package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.analyzers.{AnalyzerDefinition, NormalizerDefinition}
import com.sksamuel.elastic4s.mappings.MappingDefinition
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration._

case class IndexAliasDefinition(name: String, filter: Option[QueryDefinition] = None, routing: Option[String] = None)

case class CreateIndexDefinition(name: String,
                                 analysis: Option[AnalysisDefinition] = None,
                                 mappings: Seq[MappingDefinition] = Nil,
                                 rawSource: Option[String] = None,
                                 waitForActiveShards: Option[Int] = None,
                                 aliases: Set[IndexAliasDefinition] = Set.empty,
                                 settings: IndexSettings = new IndexSettings) {

  def alias(name: String): CreateIndexDefinition = alias(IndexAliasDefinition(name, None))
  def alias(name: String, filter: QueryDefinition): CreateIndexDefinition =
    alias(IndexAliasDefinition(name, Option(filter)))
  def alias(definition: IndexAliasDefinition): CreateIndexDefinition = copy(aliases = aliases + definition)

  def singleShard(): CreateIndexDefinition   = shards(1)
  def singleReplica(): CreateIndexDefinition = replicas(1)

  def waitForActiveShards(shards: Int): CreateIndexDefinition = copy(waitForActiveShards = shards.some)

  def shards(shds: Int): CreateIndexDefinition    = copy(settings = settings.shards = shds)
  def replicas(repls: Int): CreateIndexDefinition = copy(settings = settings.replicas = repls)

  def refreshInterval(duration: Duration): CreateIndexDefinition = refreshInterval(duration.toMillis + "ms")
  def refreshInterval(interval: String): CreateIndexDefinition   = copy(settings = settings.refreshInterval = interval)

  def settings(map: Map[String, Any]): CreateIndexDefinition =
    copy(settings = map.foldLeft(new IndexSettings()) { case (setting, (key, value)) => setting.add(key, value) })

  def indexSetting(name: String, value: Any): CreateIndexDefinition = copy(settings = settings.add(name, value))

  def mappings(first: MappingDefinition, rest: MappingDefinition*): CreateIndexDefinition = mappings(first +: rest)
  def mappings(mappings: Iterable[MappingDefinition]): CreateIndexDefinition =
    copy(mappings = this.mappings ++ mappings)

  def analysis(first: AnalyzerDefinition, rest: AnalyzerDefinition*): CreateIndexDefinition = analysis(first +: rest)
  def analysis(analyzers: Iterable[AnalyzerDefinition]): CreateIndexDefinition              = analysis(analyzers, Nil)
  def analysis(analyzers: Iterable[AnalyzerDefinition],
               normalizers: Iterable[NormalizerDefinition]): CreateIndexDefinition =
    analysis match {
      case None    => copy(analysis = AnalysisDefinition(analyzers, normalizers).some)
      case Some(a) => copy(analysis = AnalysisDefinition(a.analyzers ++ analyzers, a.normalizers ++ normalizers).some)
    }

  def normalizers(first: NormalizerDefinition, rest: NormalizerDefinition*): CreateIndexDefinition =
    analysis(Nil, first +: rest)
  def normalizers(normalizers: Iterable[NormalizerDefinition]): CreateIndexDefinition = analysis(Nil, normalizers)

  def source(source: String): CreateIndexDefinition = copy(rawSource = source.some)
}
