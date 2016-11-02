package com.sksamuel.elastic4s.search

import org.elasticsearch.action.search.MultiSearchResponse

case class MultiSearchResultItem(item: MultiSearchResponse.Item) {
  def isFailure: Boolean = item.isFailure
  def failureMessage: Option[String] = Option(item.getFailureMessage)
  def failure: Option[Throwable] = Option(item.getFailure)
  def response: Option[RichSearchResponse] = Option(item.getResponse).map(RichSearchResponse.apply)
}
