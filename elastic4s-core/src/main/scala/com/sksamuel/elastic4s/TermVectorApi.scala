package com.sksamuel.elastic4s

import org.elasticsearch.action.termvectors.TermVectorsRequest.FilterSettings
import org.elasticsearch.action.termvectors.{TermVectorsRequestBuilder, TermVectorsResponse}
import org.elasticsearch.client.Client
import org.elasticsearch.index.VersionType

import scala.concurrent.Future

trait TermVectorApi {

  implicit object TermVectorExecutable
    extends Executable[TermVectorsDefinition, TermVectorsResponse, TermVectorsResult] {
    override def apply(client: Client, t: TermVectorsDefinition): Future[TermVectorsResult] = {
      injectFutureAndMap(t.build(client.prepareTermVectors).execute)(TermVectorsResult.apply)
    }
  }
}

case class TermVectorsResult(original: TermVectorsResponse) {

  import scala.concurrent.duration._

  def fields = original.getFields
  def id: String = original.getId
  def index: String = original.getIndex
  def took: FiniteDuration = original.getTookInMillis.millis
  def `type`: String = original.getType
  def version: Long = original.getVersion
}

case class TermVectorsDefinition(indexAndTypes: IndexAndTypes,
                                 id: String,
                                 dfs: Option[Boolean] = None,
                                 fieldStatistics: Option[Boolean] = None,
                                 offsets: Option[Boolean] = None,
                                 parent: Option[String] = None,
                                 payloads: Option[Boolean] = None,
                                 positions: Option[Boolean] = None,
                                 preference: Option[String] = None,
                                 realtime: Option[Boolean] = None,
                                 routing: Option[String] = None,
                                 fields: Option[Seq[String]] = None,
                                 termStatistics: Option[Boolean] = None,
                                 version: Option[Long] = None,
                                 versionType: Option[VersionType] = None,
                                 maxNumTerms: Option[Int] = None,
                                 minTermFreq: Option[Int] = None,
                                 maxTermFreq: Option[Int] = None,
                                 minDocFreq: Option[Int] = None,
                                 maxDocFreq: Option[Int] = None,
                                 minWordLength: Option[Int] = None,
                                 maxWordLength: Option[Int] = None) {
  def build(request: TermVectorsRequestBuilder): TermVectorsRequestBuilder = {
    request.setId(id)
    request.setIndex(indexAndTypes.index)
    request.setType(indexAndTypes.types.head)
    dfs.foreach(request.setDfs)
    fieldStatistics.foreach(request.setFieldStatistics)
    offsets.foreach(request.setOffsets)
    parent.foreach(request.setParent)
    payloads.foreach(request.setPayloads)
    positions.foreach(request.setPositions)
    preference.foreach(request.setPreference)
    realtime.foreach(b => request.setRealtime(java.lang.Boolean.valueOf(b)))
    routing.foreach(request.setRouting)
    fields.foreach(fields => request.setSelectedFields(fields: _*))
    termStatistics.foreach(request.setTermStatistics)
    version.foreach(request.setVersion)
    versionType.foreach(request.setVersionType)

    val settings = new FilterSettings()
    maxNumTerms.foreach(settings.maxNumTerms = _)
    minTermFreq.foreach(settings.minTermFreq = _)
    maxTermFreq.foreach(settings.maxTermFreq = _)
    minDocFreq.foreach(settings.minDocFreq = _)
    maxDocFreq.foreach(settings.maxDocFreq = _)
    minWordLength.foreach(settings.minWordLength = _)
    maxWordLength.foreach(settings.maxWordLength = _)

    request.setFilterSettings(settings)

    request
  }

  def dfs(boolean: Boolean): TermVectorsDefinition = copy(dfs = Option(boolean))
  def fieldStatistics(boolean: Boolean): TermVectorsDefinition = copy(fieldStatistics = Option(boolean))
  def offsets(boolean: Boolean): TermVectorsDefinition = copy(offsets = Option(boolean))
  def parent(str: String): TermVectorsDefinition = copy(parent = Option(str))
  def payloads(boolean: Boolean): TermVectorsDefinition = copy(payloads = Option(boolean))
  def positions(boolean: Boolean): TermVectorsDefinition = copy(positions = Option(boolean))
  def preference(str: String): TermVectorsDefinition = copy(preference = Option(str))
  def realtime(boolean: Boolean): TermVectorsDefinition = copy(realtime = Option(boolean))
  def routing(str: String): TermVectorsDefinition = copy(routing = Option(str))
  def fields(fields: Iterable[String]): TermVectorsDefinition = copy(fields = Option(fields.toSeq))
  def fields(fields: String*): TermVectorsDefinition = copy(fields = Option(fields.toSeq))
  def termStatistics(boolean: Boolean): TermVectorsDefinition = copy(termStatistics = Option(boolean))
  def version(version: Long): TermVectorsDefinition = copy(version = Option(version))

  def maxNumTerms(maxNumTerms: Int): TermVectorsDefinition = copy(maxNumTerms = Option(maxNumTerms))
  def minTermFreq(minTermFreq: Int): TermVectorsDefinition = copy(minTermFreq = Option(minTermFreq))
  def maxTermFreq(maxTermFreq: Int): TermVectorsDefinition = copy(maxTermFreq = Option(maxTermFreq))
  def minDocFreq(minDocFreq: Int): TermVectorsDefinition = copy(minDocFreq = Option(minDocFreq))
  def maxDocFreq(maxDocFreq: Int): TermVectorsDefinition = copy(maxDocFreq = Option(maxDocFreq))
  def minWordLength(minWordLength: Int): TermVectorsDefinition = copy(minWordLength = Option(minWordLength))
  def maxWordLength(maxWordLength: Int): TermVectorsDefinition = copy(maxWordLength = Option(maxWordLength))
}
