package com.sksamuel.elastic4s.get

import com.sksamuel.elastic4s.{Hit, HitField, HitReader}
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.common.bytes.BytesReference
import org.elasticsearch.index.get.GetField

import scala.collection.JavaConverters._

case class RichGetResponse(original: GetResponse) extends Hit {

  // java method aliases
  @deprecated("use .java", "5.0.0")
  def getField(name: String): GetField = original.getField(name)

  @deprecated("use source", "5.0.0")
  def getFields = original.getFields

  @deprecated("use .java", "5.0.0")
  def getId: String = id

  @deprecated("use .java", "5.0.0")
  def getIndex: String = index

  @deprecated("use .java", "5.0.0")
  def getType: String = `type`

  @deprecated("use .java", "5.0.0")
  def getVersion: Long = version

  @deprecated("use .exists", "5.0.0")
  def isExists: Boolean = exists

  override def id: String = original.getId
  override def index: String = original.getIndex
  override def `type`: String = original.getType
  override def version: Long = original.getVersion

  def to[T: HitReader]: T = safeTo[T].fold(msg => sys.error(msg), t => t)
  def safeTo[T](implicit reader: HitReader[T]): Either[String, T] = reader.read(this)

  private def getFieldToHitField(f: GetField) = new HitField {
    override def name: String = f.getName
    override def value: AnyRef = f.getValue
    override def values: Seq[AnyRef] = Option(f.getValues).map(_.asScala).getOrElse(Nil)
    override def isMetadataField: Boolean = f.isMetadataField
  }

  @deprecated("use source instead", "5.0.0")
  def field(name: String): HitField = getFieldToHitField(original.getField(name))

  @deprecated("use source instead", "5.0.0")
  def fieldOpt(name: String): Option[HitField] = Option(original.getField(name)).map(getFieldToHitField)

  @deprecated("use source instead", "5.0.0")
  def fields: Map[String, HitField] = {
    Option(original.getFields).fold(Map.empty[String, HitField])(_.asScala.toMap.mapValues(getFieldToHitField))
  }

  @deprecated("use .sourceAsMap", "5.0.0")
  def source = sourceAsMap

  override def sourceAsMap: Map[String, AnyRef] = Option(original.getSource).map(_.asScala.toMap).getOrElse(Map.empty)
  override def sourceAsBytes: Array[Byte] = original.getSourceAsBytes
  override def sourceAsByteRef: BytesReference = original.getSourceAsBytesRef
  override def sourceAsString: String = original.getSourceAsString
  override def isSourceEmpty: Boolean = original.isSourceEmpty

  override def exists: Boolean = original.isExists

  @deprecated("Use the source methods instead", "5.0.0")
  def iterator: Iterator[GetField] = original.iterator.asScala
}
