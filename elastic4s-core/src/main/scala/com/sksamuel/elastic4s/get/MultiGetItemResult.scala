package com.sksamuel.elastic4s.get

import com.sksamuel.elastic4s.DocumentRef
import org.elasticsearch.action.get.{GetResponse, MultiGetItemResponse, MultiGetResponse}

import scala.util.{Failure, Success, Try}

case class MultiGetItemResult(original: MultiGetItemResponse) {

  @deprecated("use id", "3.0.0")
  def getId = original.getId

  @deprecated("use index", "3.0.0")
  def getIndex = original.getIndex

  @deprecated("use `type`lol", "3.0.0")
  def getType = original.getType

  @deprecated("use failed", "3.0.0")
  def isFailed = original.isFailed

  @deprecated("use response or result", "3.0.0")
  def getResponse = original.getResponse

  @deprecated("use failure", "3.0.0")
  def getFailure = original.getFailure

  def index = original.getIndex
  def `type`: String = original.getType
  def id = original.getId
  def documentRef = DocumentRef(index, `type`, id)

  def result: Try[GetResponse] = if (failed) Failure(original.getFailure.getFailure) else Success(original.getResponse)

  def response: RichGetResponse = responseOpt.get
  def responseOpt: Option[RichGetResponse] = Option(original.getResponse).map(RichGetResponse.apply)

  def failure: MultiGetResponse.Failure = failureOpt.get
  def failureOpt: Option[MultiGetResponse.Failure] = Option(original.getFailure)

  def exception : Exception = failure.getFailure
  def exceptionOpt: Option[Exception] = failureOpt.map(_.getFailure)

  def failed: Boolean = original.isFailed
}
