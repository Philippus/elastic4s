package com.sksamuel.elastic4s.http.get

import com.sksamuel.elastic4s.Hit
import com.sksamuel.elastic4s.get.HitField
import com.sksamuel.elastic4s.http.SourceAsContentBuilder

case class GetResponse(private val _id: String,
                       private val _index: String,
                       private val _type: String,
                       private val _version: Long,
                       found: Boolean,
                       fields: Map[String, AnyRef],
                       private val _source: Map[String, AnyRef]
                      ) extends Hit {

  override def index: String = _index
  override def `type`: String = _type
  override def id: String = _id
  override def version: Long = _version
  override def exists: Boolean = found
  override def score: Float = 0

  def source: Map[String, Any] = sourceAsMap

  def storedField(fieldName: String): HitField = storedFieldOpt(fieldName).get
  def storedFieldOpt(fieldName: String): Option[HitField] = fields.get(fieldName).map { v =>
    new HitField {
      override def values: Seq[AnyRef] = v match {
        case values: Seq[AnyRef] => values
        case value: AnyRef => Seq(value)
      }
      override def value: AnyRef = values.head
      override def name: String = fieldName
      override def isMetadataField: Boolean = ???
    }
  }

  def storedFieldsAsMap: Map[String, AnyRef] = Option(fields).getOrElse(Map.empty)
  override def sourceAsMap: Map[String, AnyRef] = Option(_source).getOrElse(Map.empty)
  override def sourceAsString: String = SourceAsContentBuilder(_source).string()

}
