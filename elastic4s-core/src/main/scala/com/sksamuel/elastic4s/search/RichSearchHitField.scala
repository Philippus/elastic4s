package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.HitField
import org.elasticsearch.search.SearchHitField

import scala.collection.JavaConverters._

case class RichSearchHitField(java: SearchHitField) extends HitField {

  // java method aliases
  @deprecated("use name", "3.0.0")
  def getName: String = name

  @deprecated("use value", "3.0.0")
  def getValue: AnyRef = value

  @deprecated("use values", "3.0.0")
  def getValues: Seq[AnyRef] = values

  override def name: String = java.name()
  override def value: AnyRef = java.getValue
  override def values: Seq[AnyRef] = java.values().asScala.toList
  override def isMetadataField: Boolean = java.isMetadataField
}
