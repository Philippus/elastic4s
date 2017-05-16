package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s._
import com.sksamuel.exts.OptionImplicits._

case class OpenIndexDefinition(indexes: Indexes)
case class CloseIndexDefinition(indexes: Indexes)
case class GetSegmentsDefinition(indexes: Indexes)
case class IndexExistsDefinition(index: String)
case class TypesExistsDefinition(indexes: Seq[String], types: Seq[String])
case class AliasExistsDefinition(alias: String)
case class IndicesStatsDefinition(indexes: Indexes)

case class IndicesOptions(allowNoIndices: Boolean = false,
                          ignoreUnavailable: Boolean = false,
                          expandWildcardsOpen: Boolean = false,
                          expandWildcardClosed: Boolean = false,
                          allowAliasesToMultipleIndices: Boolean = true,
                          forbidClosedIndices: Boolean = false)

case class ClearCacheDefinition(indexes: Seq[String],
                                fieldDataCache: Option[Boolean] = None,
                                requestCache: Option[Boolean] = None,
                                indicesOptions: Option[IndicesOptions] = None,
                                queryCache: Option[Boolean] = None,
                                fields: Seq[String] = Nil)

case class FlushIndexDefinition(indexes: Seq[String],
                                waitIfOngoing: Option[Boolean] = None,
                                force: Option[Boolean] = None) {
  def force(force: Boolean): FlushIndexDefinition = copy(force = force.some)
  def waitIfOngoing(waitIfOngoing: Boolean): FlushIndexDefinition = copy(waitIfOngoing = waitIfOngoing.some)
}

case class RefreshIndexDefinition(indexes: Seq[String])

case class UpdateIndexLevelSettingsDefinition(indexes: Seq[String],
                                              numberOfReplicas: Option[Int] = None,
                                              autoExpandReplicas: Option[String] = None,
                                              refreshInterval: Option[String] = None,
                                              maxResultWindow: Option[Int] = None) {

  def numberOfReplicas(numberOfReplicas: Int): UpdateIndexLevelSettingsDefinition = copy(numberOfReplicas = numberOfReplicas.some)
  def autoExpandReplicas(autoExpandReplicas: String): UpdateIndexLevelSettingsDefinition = copy(autoExpandReplicas = autoExpandReplicas.some)
  def refreshInterval(refreshInterval: String): UpdateIndexLevelSettingsDefinition = copy(refreshInterval = refreshInterval.some)
  def maxResultWindow(maxResultWindow: Int): UpdateIndexLevelSettingsDefinition = copy(maxResultWindow = maxResultWindow.some)

}

case class IndexShardStoreDefinition(indexes: Indexes, status: Option[String] = None) {

  def status(status: String): IndexShardStoreDefinition = copy(status = status.some)

}
