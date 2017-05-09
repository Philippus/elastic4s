package com.sksamuel.elastic4s.bulk

import com.sksamuel.elastic4s.DocumentRef
import com.sksamuel.elastic4s.index.RichIndexResponse
import org.elasticsearch.action.DocWriteRequest.OpType
import org.elasticsearch.action.bulk.BulkItemResponse
import org.elasticsearch.action.bulk.BulkItemResponse.Failure
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.index.IndexResponse

case class RichBulkItemResponse(original: BulkItemResponse) {

  def failure: Failure = original.getFailure
  def failureOpt: Option[Failure] = Option(failure)

  def failureMessage: String = original.getFailureMessage
  def failureMessageOpt: Option[String] = Option(failureMessage)

  def index: String = original.getIndex
  def `type`: String = original.getType
  def id: String = original.getId
  def ref = DocumentRef(index, `type`, id)
  def version: Long = original.getVersion

  def itemId: Int = original.getItemId
  def opType: OpType = original.getOpType

  @deprecated("use toDeleteResult", "5.0.0")
  def deleteResponse(): Option[DeleteResponse] = original.getResponse match {
    case d: DeleteResponse => Some(d)
    case _ => None
  }

  @deprecated("use toIndexResult", "5.0.0")
  def indexResult: Option[RichIndexResponse] = toIndexResult
  def toIndexResult: Option[RichIndexResponse] = original.getResponse match {
    case i: IndexResponse => Some(RichIndexResponse(i))
    case _ => None
  }

  def isFailure: Boolean = original.isFailed
}
