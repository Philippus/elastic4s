package com.sksamuel.elastic4s.get

import com.sksamuel.elastic4s.{DocumentRef, HitReader}
import org.elasticsearch.action.get.{MultiGetItemResponse, MultiGetResponse}

import scala.util.{Failure, Success, Try}

case class MultiGetItemResult(original: MultiGetItemResponse) {

  @deprecated("use id", "5.0.0")
  def getId = original.getId

  @deprecated("use index", "5.0.0")
  def getIndex = original.getIndex

  @deprecated("use `type`lol", "5.0.0")
  def getType = original.getType

  @deprecated("use failed", "5.0.0")
  def isFailed = original.isFailed

  @deprecated("use response or result", "5.0.0")
  def getResponse = original.getResponse

  @deprecated("use failure", "5.0.0")
  def getFailure = original.getFailure

  def index = original.getIndex
  def `type`: String = original.getType
  def id = original.getId
  def documentRef = DocumentRef(index, `type`, id)

  def to[T: HitReader]: T = responseTry match {
    case Success(get) => get.to
    case Failure(e) => throw e
  }

  def safeTo[T: HitReader]: Either[Throwable, T] = responseTry match {
    case Success(get) => get.safeTo
    case Failure(e) => Left(e)
  }

  def response: RichGetResponse = responseOpt.get
  def responseOpt: Option[RichGetResponse] = Option(original.getResponse).map(RichGetResponse.apply)
  def responseTry: Try[RichGetResponse] =
    if (failed) Failure(original.getFailure.getFailure) else Success(RichGetResponse(original.getResponse))

  def failure: MultiGetResponse.Failure = failureOpt.get
  def failureOpt: Option[MultiGetResponse.Failure] = Option(original.getFailure)

  def exception : Exception = failure.getFailure
  def exceptionOpt: Option[Exception] = failureOpt.map(_.getFailure)

  def failed: Boolean = original.isFailed
  def exists: Boolean = responseOpt.exists(_.exists)
}
