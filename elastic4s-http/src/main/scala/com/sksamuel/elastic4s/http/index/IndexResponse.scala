package com.sksamuel.elastic4s.http.index

import com.sksamuel.elastic4s.DocumentRef
import com.sksamuel.elastic4s.get.HitField

case class IndexResponse(private val _id: String,
                         private val _index: String,
                         private val _type: String,
                         private val _version: Long,
                         found: Boolean,
                         totalHits: Long,
                         private val fields: Map[String, Any],
                         private val _source: Map[String, Any]
                        ) {

  def index = _index
  def `type` = _type
  def id = _id
  def version = _version
  def ref = DocumentRef(index, `type`, id)
  def exists = found
  def source = sourceAsMap
  def storedField(fieldName: String): HitField = new HitField {
    override def values: Seq[AnyRef] = fields(fieldName) match {
      case values: Seq[AnyRef] => values
      case values: Array[AnyRef] => values
      case value: AnyRef => Seq(value)
    }
    override def value: AnyRef = values.head
    override def name: String = fieldName
    override def isMetadataField: Boolean = ???
  }
  def storedFieldsAsMap = Option(fields).getOrElse(Map.empty)
  def sourceAsMap = Option(_source).getOrElse(Map.empty)
}
