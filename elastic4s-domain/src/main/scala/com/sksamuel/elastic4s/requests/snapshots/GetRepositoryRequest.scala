package com.sksamuel.elastic4s.requests.snapshots

import com.sksamuel.elastic4s.ext.OptionImplicits._

case class GetRepositoryRequest(
    name: String,
    local: Option[Boolean] = None
) {
  def local(l: Boolean): GetRepositoryRequest = copy(local = l.some)
}
