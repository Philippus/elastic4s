package com.sksamuel.elastic4s

case class DocumentRef(index: String, `type`: String, id: String) {
  def indexAndType: IndexAndType = IndexAndType(index, `type`)
}
