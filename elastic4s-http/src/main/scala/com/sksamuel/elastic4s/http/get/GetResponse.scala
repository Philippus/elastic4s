package com.sksamuel.elastic4s.http.get

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.Hit
import com.sksamuel.elastic4s.get.HitField
import com.sksamuel.elastic4s.http.SourceAsContentBuilder

case class GetResponse(@JsonProperty("_id") id: String,
                       @JsonProperty("_index") index: String,
                       @JsonProperty("_type") `type`: String,
                       @JsonProperty("_version") version: Long,
                       found: Boolean,
                       fields: Map[String, AnyRef],
                       private val _source: Map[String, AnyRef]
                      ) extends Hit {

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
