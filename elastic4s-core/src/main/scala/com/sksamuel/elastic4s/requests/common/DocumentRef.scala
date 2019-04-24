package com.sksamuel.elastic4s.requests.common

case class DocumentRef(index: String, `type`: Option[String] = None, id: String)

object DocumentRef {
  @deprecated("types are deprecated now", "7.0")
  def apply(index: String, `type`: String, id: String): DocumentRef = DocumentRef(index, Option(`type`), id)
}
