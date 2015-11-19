package com.sksamuel.elastic4s

import org.elasticsearch.action.termvectors.TermVectorsRequest.FilterSettings
import org.elasticsearch.action.termvectors.{TermVectorsRequestBuilder, TermVectorsResponse}
import org.elasticsearch.client.Client
import org.elasticsearch.index.VersionType

import scala.concurrent.Future

trait TermVectorDsl {

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

case class TermVectorsDefinition(dfs: Option[Boolean] = None,
                                 fieldStatistics: Option[Boolean] = None,
                                 filterSettings: Option[FilterSettings] = None,
                                 id: Option[String] = None,
                                 index: Option[String] = None,
                                 offsets: Option[Boolean] = None,
                                 parent: Option[String] = None,
                                 payloads: Option[Boolean] = None,
                                 positions: Option[Boolean] = None,
                                 preference: Option[String] = None,
                                 realtime: Option[Boolean] = None,
                                 routing: Option[String] = None,
                                 selectedFields: Option[Seq[String]] = None,
                                 termStatistics: Option[Boolean] = None,
                                 `type`: Option[String] = None,
                                 version: Option[Long] = None,
                                 versionType: Option[VersionType] = None) {
  def build(request: TermVectorsRequestBuilder): TermVectorsRequestBuilder = {
    dfs.foreach(request.setDfs)
    fieldStatistics.foreach(request.setFieldStatistics)
    filterSettings.foreach(request.setFilterSettings)
    id.foreach(request.setId)
    index.foreach(request.setIndex)
    offsets.foreach(request.setOffsets)
    parent.foreach(request.setParent)
    payloads.foreach(request.setPayloads)
    positions.foreach(request.setPositions)
    preference.foreach(request.setPreference)
    realtime.foreach(b => request.setRealtime(java.lang.Boolean.valueOf(b)))
    routing.foreach(request.setRouting)
    selectedFields.foreach(fields => request.setSelectedFields(fields: _*))
    termStatistics.foreach(request.setTermStatistics)
    `type`.foreach(request.setType)
    version.foreach(request.setVersion)
    versionType.foreach(request.setVersionType)
    request
  }

  def dfs(boolean: Boolean): TermVectorsDefinition = copy(dfs = Option(boolean))
  def fieldStatistics(boolean: Boolean): TermVectorsDefinition = copy(fieldStatistics = Option(boolean))
  def filterSettings(fs: FilterSettings): TermVectorsDefinition = copy(filterSettings = Option(fs))
  def id(str: String): TermVectorsDefinition = copy(id = Option(str))
  def index(str: String): TermVectorsDefinition = copy(index = Option(str))
  def offsets(boolean: Boolean): TermVectorsDefinition = copy(offsets = Option(boolean))
  def parent(str: String): TermVectorsDefinition = copy(parent = Option(str))
  def payloads(boolean: Boolean): TermVectorsDefinition = copy(payloads = Option(boolean))
  def positions(boolean: Boolean): TermVectorsDefinition = copy(positions = Option(boolean))
  def preference(str: String): TermVectorsDefinition = copy(preference = Option(str))
  def realtime(boolean: Boolean): TermVectorsDefinition = copy(realtime = Option(boolean))
  def routing(str: String): TermVectorsDefinition = copy(routing = Option(str))
  def selectedFields(fields: Seq[String]): TermVectorsDefinition = copy(selectedFields = Option(fields))
  def termStatistics(boolean: Boolean): TermVectorsDefinition = copy(termStatistics = Option(boolean))
  def `type`(t: String): TermVectorsDefinition = copy(`type` = Option(t))
  def version(version: Long): TermVectorsDefinition = copy(version = Option(version))
  def versionType(versionType: VersionType): TermVectorsDefinition = copy(versionType = Option(versionType))
}
