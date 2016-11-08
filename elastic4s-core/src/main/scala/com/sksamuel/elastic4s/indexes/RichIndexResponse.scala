package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.DocumentRef
import org.elasticsearch.action.DocWriteResponse.Result
import org.elasticsearch.action.index.IndexResponse

case class RichIndexResponse(original: IndexResponse) {

  @deprecated("use id", "5.0.0")
  def getId = id

  @deprecated("use `type`", "5.0.0")
  def getType = `type`

  @deprecated("use index", "5.0.0")
  def getIndex = index

  @deprecated("use version", "5.0.0")
  def getVersion = original.getVersion

  @deprecated("use created", "5.0.0")
  def isCreated: Boolean = created

  def id = original.getId
  def index = original.getIndex
  def `type` = original.getType
  def version: Long = original.getVersion
  def ref = DocumentRef(index, `type`, id)

  def result = original.getResult
  def created: Boolean = original.getResult == Result.CREATED
}
