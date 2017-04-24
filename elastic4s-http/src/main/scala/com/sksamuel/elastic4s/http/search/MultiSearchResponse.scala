package com.sksamuel.elastic4s.http.search

import com.sksamuel.elastic4s.HitReader

case class MultiSearchResponse(responses: Seq[SearchResponse]) {
  def size: Int = responses.size
  def to[T: HitReader]: IndexedSeq[T] = responses.flatMap(_.hits.hits).map(_.to[T]).toIndexedSeq
  def safeTo[T: HitReader]: IndexedSeq[Either[Throwable, T]] = responses.flatMap(_.hits.hits).map(_.safeTo[T]).toIndexedSeq
}
