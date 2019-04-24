package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.exts.OptionImplicits._

case class GetMappingRequest(indexes: Indexes, local: Option[Boolean] = None) {
  def local(local: Boolean): GetMappingRequest = copy(local = local.some)
}
