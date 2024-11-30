package com.sksamuel.elastic4s.requests.get

import com.sksamuel.elastic4s.HitReader

import scala.util.Try

case class MultiGetResponse(docs: Seq[GetResponse]) {
  def items: Seq[GetResponse] = docs
  def size: Int               = docs.size

  def to[T: HitReader]: IndexedSeq[T]          = docs.map(_.to[T]).toIndexedSeq
  def safeTo[T: HitReader]: IndexedSeq[Try[T]] = docs.map(_.safeTo[T]).toIndexedSeq
}
