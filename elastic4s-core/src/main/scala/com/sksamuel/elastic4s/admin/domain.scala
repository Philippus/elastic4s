package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s._
import com.sksamuel.exts.OptionImplicits._

case class OpenIndexRequest(indexes: Indexes)
case class CloseIndexRequest(indexes: Indexes)
case class GetSegmentsRequest(indexes: Indexes)
case class IndicesExistsRequest(indexes: Indexes, includeTypeName:Option[Boolean]=None)
case class TypesExistsRequest(indexes: Seq[String], types: Seq[String])
case class AliasExistsRequest(alias: String)
case class IndexStatsRequest(indices: Indexes)

case class IndicesOptionsRequest(allowNoIndices: Boolean = false,
                                 ignoreUnavailable: Boolean = false,
                                 expandWildcardsOpen: Boolean = false,
                                 expandWildcardClosed: Boolean = false)

case class ClearCacheRequest(indexes: Seq[String],
                             fieldDataCache: Option[Boolean] = None,
                             requestCache: Option[Boolean] = None,
                             indicesOptions: Option[IndicesOptionsRequest] = None,
                             queryCache: Option[Boolean] = None,
                             fields: Seq[String] = Nil)

case class FlushIndexRequest(indexes: Seq[String],
                             waitIfOngoing: Option[Boolean] = None,
                             force: Option[Boolean] = None) {
  def force(force: Boolean): FlushIndexRequest                 = copy(force = force.some)
  def waitIfOngoing(waitIfOngoing: Boolean): FlushIndexRequest = copy(waitIfOngoing = waitIfOngoing.some)
}

case class RefreshIndexRequest(indexes: Seq[String])

case class UpdateIndexLevelSettingsRequest(indexes: Seq[String],
                                           numberOfReplicas: Option[Int] = None,
                                           autoExpandReplicas: Option[String] = None,
                                           refreshInterval: Option[String] = None,
                                           maxResultWindow: Option[Int] = None) {

  def numberOfReplicas(numberOfReplicas: Int): UpdateIndexLevelSettingsRequest =
    copy(numberOfReplicas = numberOfReplicas.some)
  def autoExpandReplicas(autoExpandReplicas: String): UpdateIndexLevelSettingsRequest =
    copy(autoExpandReplicas = autoExpandReplicas.some)
  def refreshInterval(refreshInterval: String): UpdateIndexLevelSettingsRequest =
    copy(refreshInterval = refreshInterval.some)
  def maxResultWindow(maxResultWindow: Int): UpdateIndexLevelSettingsRequest =
    copy(maxResultWindow = maxResultWindow.some)

}

case class IndexShardStoreRequest(indexes: Indexes, status: Option[String] = None) {
  def status(status: String): IndexShardStoreRequest = copy(status = status.some)
}
