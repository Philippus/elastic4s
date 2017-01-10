package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.DocumentRef
import org.elasticsearch.action.DocWriteResponse.Result
import org.elasticsearch.action.update.UpdateResponse

case class RichUpdateResponse(original: UpdateResponse) {
  def result = original.getResult
  def get = original.getGetResult
  def status = original.status()
  def index = original.getIndex
  def `type` = original.getType
  def id = original.getId
  def ref: DocumentRef = DocumentRef(index, `type`, id)
  def shardId = original.getShardId
  def shardInfo = original.getShardInfo
  def version = original.getVersion
  def created: Boolean = original.getResult == Result.CREATED
}
