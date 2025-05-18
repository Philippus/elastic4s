package com.sksamuel.elastic4s.requests.common

import com.sksamuel.elastic4s.Index

case class DocumentRef(index: Index, `type`: Option[String] = None, id: String)

object DocumentRef {
  def apply(index: String, id: String): DocumentRef = DocumentRef(Index(index), None, id)

  def apply(index: Index, id: String): DocumentRef = DocumentRef(index, None, id)
}
