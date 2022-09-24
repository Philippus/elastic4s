package com.sksamuel.elastic4s.requests.settings

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.requests.admin.IndicesOptionsRequest
import com.sksamuel.elastic4s.ext.OptionImplicits._

case class GetSettingsRequest(indexes: Indexes, options: Option[IndicesOptionsRequest] = None) {
  def options(options: IndicesOptionsRequest): GetSettingsRequest = copy(options = options.some)
}
