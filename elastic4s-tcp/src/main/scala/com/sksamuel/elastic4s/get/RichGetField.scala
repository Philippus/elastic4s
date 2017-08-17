package com.sksamuel.elastic4s.get

import org.elasticsearch.common.document.DocumentField

import scala.collection.JavaConverters._

case class RichGetField(original: DocumentField) extends HitField {

  override def name: String = original.getName
  override def value: AnyRef = original.getValue
  override def values: Seq[AnyRef] = original.getValues.asScala
  override def isMetadataField: Boolean = original.isMetadataField

  def iterator: Iterator[AnyRef] = original.iterator.asScala
}
