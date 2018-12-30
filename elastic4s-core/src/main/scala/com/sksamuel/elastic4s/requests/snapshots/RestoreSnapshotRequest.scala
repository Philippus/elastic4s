package com.sksamuel.elastic4s.requests.snapshots

import com.sksamuel.elastic4s.{Index, Indexes}
import com.sksamuel.exts.OptionImplicits._

case class RestoreSnapshotRequest(snapshotName: String,
                                  repositoryName: String,
                                  indices: Indexes = Indexes.Empty,
                                  ignoreUnavailable: Option[Boolean] = None,
                                  includeGlobalState: Option[Boolean] = None,
                                  renamePattern: Option[String] = None,
                                  renameReplacement: Option[String] = None,
                                  partial: Option[Boolean] = None,
                                  includeAliases: Option[Boolean] = None,
                                  waitForCompletion: Option[Boolean] = None) {
  require(snapshotName.nonEmpty, "snapshot name must not be null or empty")
  require(repositoryName.nonEmpty, "repo must not be null or empty")

  def partial(partial: Boolean): RestoreSnapshotRequest               = copy(partial = partial.some)
  def includeAliases(includeAliases: Boolean): RestoreSnapshotRequest = copy(includeAliases = includeAliases.some)
  def includeGlobalState(global: Boolean): RestoreSnapshotRequest     = copy(includeGlobalState = global.some)
  def waitForCompletion(wait: Boolean): RestoreSnapshotRequest        = copy(waitForCompletion = wait.some)
  def renamePattern(pattern: String): RestoreSnapshotRequest          = copy(renamePattern = pattern.some)
  def renameReplacement(str: String): RestoreSnapshotRequest          = copy(renameReplacement = str.some)

  def index(index: Index): RestoreSnapshotRequest       = copy(indices = index.toIndexes)
  def indices(indices: Indexes): RestoreSnapshotRequest = copy(indices = indices)
}
