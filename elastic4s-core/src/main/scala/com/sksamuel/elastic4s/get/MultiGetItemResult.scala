package com.sksamuel.elastic4s.get

import org.elasticsearch.action.get.{GetResponse, MultiGetItemResponse, MultiGetResponse}

case class MultiGetItemResult(original: MultiGetItemResponse) {

  def getId = original.getId
  def getIndex = original.getIndex

  def getType = original.getType
  def isFailed = original.isFailed

  def failure: Option[MultiGetResponse.Failure] = Option(original.getFailure)
  def id = original.getId
  def index = original.getIndex
  def response: Option[GetResponse] = Option(original.getResponse)
  def `type`: String = original.getType
  def failed: Boolean = original.isFailed
}
