package com.sksamuel.elastic4s.indexes

import com.sksamuel.exts.OptionImplicits._

case class GetIndexRequest(index: String, includeTypeName: Option[Boolean] = None) {

  def includeTypeName(includeTypeName: Boolean): GetIndexRequest = copy(includeTypeName = includeTypeName.some)
}
