package com.sksamuel.elastic4s.requests.common

import com.sksamuel.elastic4s.Index

case class DocumentRef(index: Index, `type`: Option[String] = None, id: String)

object DocumentRef {

  @deprecated("types are deprecated now, so use DocumentRef(index, id)", "7.0")
  def apply(index: String, `type`: String, id: String): DocumentRef = DocumentRef(Index(index), Option(`type`), id)

  @deprecated("types are deprecated now, so use DocumentRef(index, id)", "7.0")
  def apply(index: String, `type`: Option[String], id: String): DocumentRef = DocumentRef(Index(index), `type`, id)

  def apply(index: String, id: String): DocumentRef = DocumentRef(Index(index), None, id)

  def apply(index: Index, id: String): DocumentRef = DocumentRef(index, None, id)
}
