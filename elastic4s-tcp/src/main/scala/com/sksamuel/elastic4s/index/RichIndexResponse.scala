package com.sksamuel.elastic4s.index

import com.sksamuel.elastic4s.DocumentRef
import org.elasticsearch.action.DocWriteResponse.Result
import org.elasticsearch.action.index.IndexResponse

case class RichIndexResponse(original: IndexResponse) {

  def id: String = original.getId
  def index: String = original.getIndex
  def `type`: String = original.getType
  def version: Long = original.getVersion
  def ref = DocumentRef(index, `type`, id)

  def result: Result = original.getResult
  def created: Boolean = original.getResult == Result.CREATED
}
