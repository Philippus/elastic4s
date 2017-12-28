package com.sksamuel.elastic4s.snapshots

import com.sksamuel.elastic4s.{Index, Indexes}
import com.sksamuel.exts.OptionImplicits._

case class RestoreSnapshot(snapshotName: String,
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

  def partial(partial: Boolean): RestoreSnapshot = copy(partial = partial.some)
  def includeAliases(includeAliases: Boolean): RestoreSnapshot = copy(includeAliases = includeAliases.some)
  def includeGlobalState(global: Boolean): RestoreSnapshot = copy(includeGlobalState = global.some)
  def waitForCompletion(wait: Boolean): RestoreSnapshot = copy(waitForCompletion = wait.some)
  def renamePattern(pattern: String): RestoreSnapshot = copy(renamePattern = pattern.some)
  def renameReplacement(str: String): RestoreSnapshot = copy(renameReplacement = str.some)

  def index(index: Index): RestoreSnapshot = copy(indices = index.toIndexes)
  def indices(indices: Indexes): RestoreSnapshot = copy(indices = indices)
}
