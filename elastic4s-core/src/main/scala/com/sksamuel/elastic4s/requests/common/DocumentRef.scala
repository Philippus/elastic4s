package com.sksamuel.elastic4s.requests.common

import com.sksamuel.elastic4s.IndexAndType

case class DocumentRef(index: String, `type`: String, id: String) {
  def indexAndType: IndexAndType = IndexAndType(index, `type`)
}
