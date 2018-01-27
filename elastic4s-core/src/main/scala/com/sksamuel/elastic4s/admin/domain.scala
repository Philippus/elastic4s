package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s._
import com.sksamuel.exts.OptionImplicits._

case class OpenIndex(indexes: Indexes)
case class CloseIndex(indexes: Indexes)
case class GetSegments(indexes: Indexes)
case class IndicesExists(indexes: Indexes)
case class TypesExists(indexes: Seq[String], types: Seq[String])
case class AliasExistsDefinition(alias: String)
case class IndexStats(indices: Indexes)

case class IndicesOptions(allowNoIndices: Boolean = false,
                          ignoreUnavailable: Boolean = false,
                          expandWildcardsOpen: Boolean = false,
                          expandWildcardClosed: Boolean = false)

case class ClearCache(indexes: Seq[String],
                      fieldDataCache: Option[Boolean] = None,
                      requestCache: Option[Boolean] = None,
                      indicesOptions: Option[IndicesOptions] = None,
                      queryCache: Option[Boolean] = None,
                      fields: Seq[String] = Nil)

case class FlushIndex(indexes: Seq[String], waitIfOngoing: Option[Boolean] = None, force: Option[Boolean] = None) {
  def force(force: Boolean): FlushIndex                 = copy(force = force.some)
  def waitIfOngoing(waitIfOngoing: Boolean): FlushIndex = copy(waitIfOngoing = waitIfOngoing.some)
}

case class RefreshIndex(indexes: Seq[String])

case class UpdateIndexLevelSettingsDefinition(indexes: Seq[String],
                                              numberOfReplicas: Option[Int] = None,
                                              autoExpandReplicas: Option[String] = None,
                                              refreshInterval: Option[String] = None,
                                              maxResultWindow: Option[Int] = None) {

  def numberOfReplicas(numberOfReplicas: Int): UpdateIndexLevelSettingsDefinition =
    copy(numberOfReplicas = numberOfReplicas.some)
  def autoExpandReplicas(autoExpandReplicas: String): UpdateIndexLevelSettingsDefinition =
    copy(autoExpandReplicas = autoExpandReplicas.some)
  def refreshInterval(refreshInterval: String): UpdateIndexLevelSettingsDefinition =
    copy(refreshInterval = refreshInterval.some)
  def maxResultWindow(maxResultWindow: Int): UpdateIndexLevelSettingsDefinition =
    copy(maxResultWindow = maxResultWindow.some)

}

case class IndexShardStoreDefinition(indexes: Indexes, status: Option[String] = None) {
  def status(status: String): IndexShardStoreDefinition = copy(status = status.some)
}
