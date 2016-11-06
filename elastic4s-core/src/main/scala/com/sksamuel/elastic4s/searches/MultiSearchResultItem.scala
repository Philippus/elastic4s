package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.HitReader
import org.elasticsearch.action.search.MultiSearchResponse

case class MultiSearchResultItem(item: MultiSearchResponse.Item) {

  def isFailure: Boolean = item.isFailure
  def isSuccess: Boolean = !isFailure

  def failure: Throwable = item.getFailure
  def failureOpt: Option[Throwable] = Option(item.getFailure)

  def failureMessage: String = item.getFailureMessage
  def failureMessageOpt: Option[String] = Option(item.getFailureMessage)

  def response: RichSearchResponse = RichSearchResponse(item.getResponse)
  def responseOpt: Option[RichSearchResponse] = Option(item.getResponse).map(RichSearchResponse.apply)

  def to[T: HitReader]: IndexedSeq[T] = response.to[T]
  def safeTo[T: HitReader]: IndexedSeq[Either[Throwable, T]] = response.safeTo[T]
}
