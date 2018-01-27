package com.sksamuel.elastic4s.snapshots

import com.sksamuel.elastic4s.{Index, Indexes}
import com.sksamuel.exts.OptionImplicits._

case class CreateSnapshot(snapshotName: String,
                          repositoryName: String,
                          indices: Indexes = Indexes.Empty,
                          ignoreUnavailable: Option[Boolean] = None,
                          waitForCompletion: Option[Boolean] = None,
                          partial: Option[Boolean] = None,
                          includeGlobalState: Option[Boolean] = None) {
  require(snapshotName.nonEmpty, "snapshot name must not be null or empty")
  require(repositoryName.nonEmpty, "repo name must not be null or empty")

  def partial(p: Boolean): CreateSnapshot = copy(partial = p.some)

  def includeGlobalState(global: Boolean): CreateSnapshot = copy(includeGlobalState = global.some)
  def ignoreUnavailable(ignore: Boolean): CreateSnapshot  = copy(ignoreUnavailable = ignore.some)
  def waitForCompletion(w: Boolean): CreateSnapshot       = copy(waitForCompletion = w.some)

  def index(index: Index): CreateSnapshot       = copy(indices = index.toIndexes)
  def indices(indices: Indexes): CreateSnapshot = copy(indices = indices)
}
