package com.sksamuel.elastic4s.snapshots

import com.sksamuel.elastic4s.{Index, Indexes}
import com.sksamuel.exts.OptionImplicits._

case class RestoreSnapshotDefinition(snapshotName: String,
                                     repositoryName: String,
                                     indices: Indexes = Indexes.Empty,
                                     ignoreUnavailable: Option[Boolean] = None,
                                     includeGlobalState: Option[Boolean] = None,
                                     renamePattern: Option[String] = None,
                                     renameReplacement: Option[String] = None,
                                     partial: Option[Boolean] = None,
                                     includeAliases: Option[Boolean] = None,
                                     waitForCompletion: Option[Boolean] = None
                                    ) {
  require(snapshotName.nonEmpty, "snapshot name must not be null or empty")
  require(repositoryName.nonEmpty, "repo must not be null or empty")

  def partial(partial: Boolean): RestoreSnapshotDefinition = copy(partial = partial.some)
  def includeAliases(includeAliases: Boolean): RestoreSnapshotDefinition = copy(includeAliases = includeAliases.some)
  def includeGlobalState(global: Boolean): RestoreSnapshotDefinition = copy(includeGlobalState = global.some)
  def waitForCompletion(wait: Boolean): RestoreSnapshotDefinition = copy(waitForCompletion = wait.some)
  def renamePattern(pattern: String): RestoreSnapshotDefinition = copy(renamePattern = pattern.some)
  def renameReplacement(str: String): RestoreSnapshotDefinition = copy(renameReplacement = str.some)

  def index(index: Index): RestoreSnapshotDefinition = copy(indices = index.toIndexes)
  def indices(indices: Indexes): RestoreSnapshotDefinition = copy(indices = indices)
}
