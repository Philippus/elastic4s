package com.sksamuel.elastic4s.bulk

import com.sksamuel.elastic4s.indexes.IndexResult
import org.elasticsearch.action.bulk.BulkItemResponse
import org.elasticsearch.action.bulk.BulkItemResponse.Failure
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.index.IndexResponse

case class BulkItemResult(original: BulkItemResponse) {

  def failure: Failure = original.getFailure
  def failureOpt: Option[Failure] = Option(failure)

  def failureMessage: String = original.getFailureMessage
  def failureMessageOpt: Option[String] = Option(failureMessage)

  def id = original.getId
  def index = original.getIndex
  def itemId = original.getItemId
  def opType = original.getOpType

  @deprecated("use toDeleteResult", "5.0.0")
  def deleteResponse: Option[DeleteResponse] = original.getResponse match {
    case d: DeleteResponse => Some(d)
    case _ => None
  }

  @deprecated("use toIndexResult", "5.0.0")
  def indexResult: Option[IndexResult] = toIndexResult
  def toIndexResult: Option[IndexResult] = original.getResponse match {
    case i: IndexResponse => Some(IndexResult(i))
    case _ => None
  }

  def `type` = original.getType
  def version = original.getVersion
  def isFailure: Boolean = original.isFailed
}
