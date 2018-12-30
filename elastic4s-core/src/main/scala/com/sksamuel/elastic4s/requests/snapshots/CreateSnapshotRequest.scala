package com.sksamuel.elastic4s.requests.snapshots

import com.sksamuel.elastic4s.{Index, Indexes}
import com.sksamuel.exts.OptionImplicits._

case class CreateSnapshotRequest(snapshotName: String,
                                 repositoryName: String,
                                 indices: Indexes = Indexes.Empty,
                                 ignoreUnavailable: Option[Boolean] = None,
                                 waitForCompletion: Option[Boolean] = None,
                                 partial: Option[Boolean] = None,
                                 includeGlobalState: Option[Boolean] = None) {
  require(snapshotName.nonEmpty, "snapshot name must not be null or empty")
  require(repositoryName.nonEmpty, "repo name must not be null or empty")

  def partial(p: Boolean): CreateSnapshotRequest = copy(partial = p.some)

  def includeGlobalState(global: Boolean): CreateSnapshotRequest = copy(includeGlobalState = global.some)
  def ignoreUnavailable(ignore: Boolean): CreateSnapshotRequest  = copy(ignoreUnavailable = ignore.some)
  def waitForCompletion(w: Boolean): CreateSnapshotRequest       = copy(waitForCompletion = w.some)

  def index(index: Index): CreateSnapshotRequest       = copy(indices = index.toIndexes)
  def indices(indices: Indexes): CreateSnapshotRequest = copy(indices = indices)
}
