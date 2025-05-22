package com.sksamuel.elastic4s.requests.admin

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.ext.OptionImplicits._

case class OpenIndexRequest(
    indexes: Indexes,
    ignoreUnavailable: Option[Boolean] = None,
    waitForActiveShards: Option[Int] = None
) {
  def ignoreUnavailable(ignore: Boolean): OpenIndexRequest = copy(ignoreUnavailable = Some(ignore))
  def waitForActiveShards(count: Int): OpenIndexRequest    = copy(waitForActiveShards = Some(count))
}

case class CloseIndexRequest(indexes: Indexes)
case class GetSegmentsRequest(indexes: Indexes)
case class AliasExistsRequest(alias: String)
case class IndexStatsRequest(indices: Indexes)

case class IndicesOptionsRequest(
    allowNoIndices: Boolean = false,
    ignoreUnavailable: Boolean = false,
    expandWildcardsOpen: Boolean = true, // Default ES value
    expandWildcardClosed: Boolean = false
)

case class IndicesExistsRequest(
    indexes: Indexes,
    indicesOptions: Option[IndicesOptionsRequest] = None
)

case class ClearCacheRequest(
    indexes: Seq[String],
    fieldDataCache: Option[Boolean] = None,
    requestCache: Option[Boolean] = None,
    indicesOptions: Option[IndicesOptionsRequest] = None,
    queryCache: Option[Boolean] = None,
    fields: Seq[String] = Nil
)

case class FlushIndexRequest(
    indexes: Seq[String],
    waitIfOngoing: Option[Boolean] = None,
    force: Option[Boolean] = None
) {
  def force(force: Boolean): FlushIndexRequest                 = copy(force = force.some)
  def waitIfOngoing(waitIfOngoing: Boolean): FlushIndexRequest = copy(waitIfOngoing = waitIfOngoing.some)
}

case class RefreshIndexRequest(indexes: Seq[String])

case class TranslogRequest(durability: String, syncInterval: Option[String], flushThresholdSize: Option[String])

case class UpdateIndexLevelSettingsRequest(
    indexes: Seq[String],
    numberOfReplicas: Option[Int] = None,
    autoExpandReplicas: Option[String] = None,
    refreshInterval: Option[String] = None,
    maxResultWindow: Option[Int] = None,
    translog: Option[TranslogRequest] = None,
    settings: Map[String, String] = Map.empty
) {

  def numberOfReplicas(numberOfReplicas: Int): UpdateIndexLevelSettingsRequest        =
    copy(numberOfReplicas = numberOfReplicas.some)
  def autoExpandReplicas(autoExpandReplicas: String): UpdateIndexLevelSettingsRequest =
    copy(autoExpandReplicas = autoExpandReplicas.some)
  def refreshInterval(refreshInterval: String): UpdateIndexLevelSettingsRequest       =
    copy(refreshInterval = refreshInterval.some)
  def maxResultWindow(maxResultWindow: Int): UpdateIndexLevelSettingsRequest          =
    copy(maxResultWindow = maxResultWindow.some)
  def translog(translog: TranslogRequest): UpdateIndexLevelSettingsRequest            =
    copy(translog = translog.some)
  def settings(map: Map[String, String]): UpdateIndexLevelSettingsRequest             = copy(settings = map)
}

case class IndexShardStoreRequest(indexes: Indexes, status: Option[String] = None) {
  def status(status: String): IndexShardStoreRequest = copy(status = status.some)
}
