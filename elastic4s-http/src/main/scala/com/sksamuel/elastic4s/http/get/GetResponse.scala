package com.sksamuel.elastic4s.http.get

import com.sksamuel.elastic4s.Hit
import com.sksamuel.elastic4s.get.HitField
import com.sksamuel.elastic4s.http.SourceAsContentBuilder

case class GetResponse(private val _id: String,
                       private val _index: String,
                       private val _type: String,
                       private val _version: Long,
                       found: Boolean,
                       fields: Map[String, Any],
                       private val _source: Map[String, Any]
                      ) extends Hit {

  def index: String = _index
  def `type`: String = _type
  def id: String = _id
  def version: Long = _version
  def exists: Boolean = found
  def source: Map[String, Any] = sourceAsMap

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

  def storedFieldsAsMap: Map[String, Any] = Option(fields).getOrElse(Map.empty)
  override def sourceAsMap: Map[String, Any] = Option(_source).getOrElse(Map.empty)
  override def sourceAsString: String = SourceAsContentBuilder(_source).string()

}
