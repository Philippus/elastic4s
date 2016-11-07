package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.get.HitField
import org.elasticsearch.search.SearchHitField

import scala.collection.JavaConverters._

case class RichSearchHitField(java: SearchHitField) extends HitField {

  // java method aliases
  @deprecated("use name", "5.0.0")
  def getName: String = name

  @deprecated("use value", "5.0.0")
  def getValue: AnyRef = value

  @deprecated("use values", "5.0.0")
  def getValues: Seq[AnyRef] = values

  override def name: String = java.name()
  override def value: AnyRef = java.getValue
  override def values: Seq[AnyRef] = java.values().asScala.toList
  override def isMetadataField: Boolean = java.isMetadataField
}
