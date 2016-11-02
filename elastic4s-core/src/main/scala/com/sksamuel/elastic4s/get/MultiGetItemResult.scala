package com.sksamuel.elastic4s.get

import org.elasticsearch.action.get.{GetResponse, MultiGetItemResponse, MultiGetResponse}

case class MultiGetItemResult(original: MultiGetItemResponse) {

  @deprecated("use id", "3.0.0")
  def getId = original.getId

  @deprecated("use index", "3.0.0")
  def getIndex = original.getIndex

  @deprecated("use `type`lol", "3.0.0")
  def getType = original.getType

  @deprecated("use failed", "3.0.0")
  def isFailed = original.isFailed

  def index = original.getIndex
  def `type`: String = original.getType
  def id = original.getId

  def response: Option[GetResponse] = Option(original.getResponse)
  def failure: Option[MultiGetResponse.Failure] = Option(original.getFailure)

  def failed: Boolean = original.isFailed
}
