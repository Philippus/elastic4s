package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.HitReader
import org.elasticsearch.action.search.MultiSearchResponse

case class RichMultiSearchResponse(original: MultiSearchResponse) {

  def size: Int = responses.size

  def responses: Seq[RichMultiSearchResponseItem] = original.getResponses.map(RichMultiSearchResponseItem.apply)

  // returns all the matches as a single sequence, dropping errors.
  // if you wish to retain errors use safeTo
  // if you wish to return the seq of seq, then use responses and map individually
  def to[T: HitReader]: Seq[T] = responses.flatMap(_.to[T])

  // returns all the matches as a single sequence
  // if you wish to return the seq of seq, then use responses and map individually
  def safeTo[T: HitReader]: Seq[Either[Throwable, T]] = responses.flatMap(_.safeTo[T])

  @deprecated("use items", "5.0.0")
  def getResponses(): Array[MultiSearchResponse.Item] = original.getResponses
}
