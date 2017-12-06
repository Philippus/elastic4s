package com.sksamuel.elastic4s.snapshots

import com.sksamuel.elastic4s.{Index, Indexes}
import com.sksamuel.exts.OptionImplicits._

case class CreateSnapshotDefinition(snapshotName: String,
                                    repositoryName: String,
                                    indices: Indexes = Indexes.Empty,
                                    ignoreUnavailable: Option[Boolean] = None,
                                    waitForCompletion: Option[Boolean] = None,
                                    partial: Option[Boolean] = None,
                                    includeGlobalState: Option[Boolean] = None
                                   ) {
  require(snapshotName.nonEmpty, "snapshot name must not be null or empty")
  require(repositoryName.nonEmpty, "repo name must not be null or empty")

  def partial(p: Boolean): CreateSnapshotDefinition = copy(partial = p.some)

  def includeGlobalState(global: Boolean): CreateSnapshotDefinition = copy(includeGlobalState = global.some)
  def ignoreUnavailable(ignore: Boolean): CreateSnapshotDefinition = copy(ignoreUnavailable = ignore.some)
  def waitForCompletion(w: Boolean): CreateSnapshotDefinition = copy(waitForCompletion = w.some)

  def index(index: Index): CreateSnapshotDefinition = copy(indices = index.toIndexes)
  def indices(indices: Indexes): CreateSnapshotDefinition = copy(indices = indices)
}
