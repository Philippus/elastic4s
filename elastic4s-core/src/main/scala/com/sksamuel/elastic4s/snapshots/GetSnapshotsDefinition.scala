package com.sksamuel.elastic4s.snapshots

import com.sksamuel.exts.OptionImplicits._

case class GetSnapshotsDefinition(snapshotNames: Seq[String],
                                  repositoryName: String,
                                  ignoreUnavailable: Option[Boolean] = None,
                                  verbose: Option[Boolean] = None) {
  def ignoreUnavailable(ignore: Boolean): GetSnapshotsDefinition = copy(ignoreUnavailable = ignore.some)
  def verbose(v: Boolean): GetSnapshotsDefinition = copy(verbose = v.some)
}
