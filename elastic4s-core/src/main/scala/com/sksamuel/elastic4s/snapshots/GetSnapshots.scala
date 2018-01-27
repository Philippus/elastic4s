package com.sksamuel.elastic4s.snapshots

import com.sksamuel.exts.OptionImplicits._

case class GetSnapshots(snapshotNames: Seq[String],
                        repositoryName: String,
                        ignoreUnavailable: Option[Boolean] = None,
                        verbose: Option[Boolean] = None) {
  def ignoreUnavailable(ignore: Boolean): GetSnapshots = copy(ignoreUnavailable = ignore.some)
  def verbose(v: Boolean): GetSnapshots                = copy(verbose = v.some)
}
