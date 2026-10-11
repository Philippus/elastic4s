package com.sksamuel.elastic4s.requests.snapshots

import com.sksamuel.elastic4s.ext.OptionImplicits.*

case class DeleteSnapshotRequest(
    snapshotName: String,
    repositoryName: String,
    waitForCompletion: Option[Boolean] = None
) {
  def waitForCompletion(wait: Boolean): DeleteSnapshotRequest = copy(waitForCompletion = wait.some)
}
